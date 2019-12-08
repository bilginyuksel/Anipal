const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();



function loadOldPosts(currentUserUid, followedUserUid){
    
    /**
     * find references:
     * upload followedusers old posts to followinguser post pool.
     */
    const refOldPosts = admin.database().ref("/UserPersonalPosts").child(followedUserUid);
    const refCurrentUserPosts = admin.database().ref("/UserPosts").child(currentUserUid);

    refOldPosts.once("value", (data) => {
        // console.log("Data : ",data);
        if(data.exists()){
            data.forEach(element => {
                const value = element.val();
                console.log("Element : ",value);
                refCurrentUserPosts.child(value.postUUID).set(value);
            });

        }
    });

}

function sendNotification(userUid,title, message){
    // notification send code.

    const payload = {
        notification:{
            title:title,
            body:message
        }
    };

    admin.database().ref("/Users").child(userUid).child("messageToken").once("value",data=>{
        const messageToken = data.val();
        admin.messaging().sendToDevice(messageToken,payload);
    });
}

function refundUsersMoneyDeleteDonation(postUid, userUid){
    admin.database().ref("/Users").child(userUid).once("value",data=>{
        const user = data.val();
        var price= user.donations[postUid];
        price += user.coin['coin'];
        var donators = user.donators;
        delete donators[postUid];
        // update new values.
        admin.database().ref("/Users").child(userUid).update({'coin/coin':price,'donators':donators});
    });
}

// This functions triggers onCreate, onUpdate and onDelete
// Specify the function for on delete but no need at this time.
exports.sendPostToFollowers = functions.database.ref('/Posts/{postUID}')
    .onWrite((snapshot,context) => {
        // original value added.
        console.log(snapshot.after.val())
        const originalPost = snapshot.after.val();
        const userID = originalPost.userUUID;
        //console.log(originalPost);
        // find followers
        const dbRef = admin.database().ref("/Users").child(userID);
        // do the arrow initialization
       
        // update users personal post
        admin.database().ref('/UserPersonalPosts/'+userID).child(originalPost.postUUID).set(originalPost);

        const ref = admin.database().ref("/UserPosts");
        ref.child(userID).child(originalPost.postUUID).set(originalPost);
    
        dbRef.once("value",(data)=>{
            const followers = data.val().followers;
            // Get the followers list.
            console.log(followers);
            if(followers){
                // if followers list is not none
                
                followers.forEach(element => {
                    // add the post to all followers.
                    ref.child(element).child(originalPost.postUUID).set(originalPost);
                });
                
            }
        });

    });

/*
this functions action was onWrite() if there is any problem with uploading messages to chatroom
check it 
*/ 
exports.createChatRoom = functions.database.ref("Messages/{messageUUID}")
    .onCreate((snap,context) => {
        // When a new message created !

        /* 
        also update chatrooms */
        const messageData = snap.val();
        // find sender and receiver
        const receiver = messageData.receiverUUID;
        const sender = context.auth.uid;

        const refChatRoom = admin.database().ref("/ChatRooms");
        // sender's chatroom
        refChatRoom.child(sender).child(receiver).child("messages").child(messageData.messageUUID).set(messageData);
        refChatRoom.child(sender).child(receiver).update({"lastMessage":messageData.message,"lastMessageDate":messageData.sendDate});
        // receiver's chatroom
        refChatRoom.child(receiver).child(sender).child("messages").child(messageData.messageUUID).set(messageData);
        refChatRoom.child(receiver).child(sender).update({"lastMessage":messageData.message,"lastMessageDate":messageData.sendDate});
        // increment not read
        refChatRoom.child(receiver).child(sender).child("isReadCounter").transaction(isReadCounter =>{
            if(isReadCounter || (isReadCounter===0)){
                isReadCounter++;
            }
            return isReadCounter;
        });
        refChatRoom
        /*
        send notification to receiver
        notification stuff here */
    
        admin.database().ref("/Users").child(sender).once("value",(data)=>{
            const val = data.val();
            const fullname = val.firstName + " "+val.lastName;
            sendNotification(receiver,fullname,messageData.message);
        });
        // sendNotification(receiver,"Bir mesajınız var.",messageData.message);

    });


// Control this function
exports.followUserListener = functions.database.ref("Users/{userUUID}/following/{followingUUID}")
    .onWrite((snap,context) =>{

        // get current user :: means(follower)
        const currentUserUid = context.auth.uid;
        // Get followed user uid
        const followedUid = snap.after.val();
        // find followed user and update that user's followers list.
        const refUser = admin.database().ref("/Users").child(followedUid);

        console.log({'Current User Uid : ':currentUserUid,"Followed User Uid : ":followedUid});
        var followers = []
        refUser.once("value",(data)=>{
            if(data.exists()){
                console.log("Data.val() : ",data.val());
                followers = data.val().followers;
                console.log("Data.val() Followers : ",data.val().followers);
                if(followers===undefined)
                    followers = [];
        
                console.log("Followers : ",followers);
                followers.push(currentUserUid);
                refUser.update({"followers":followers});
            } 
        });
     
        // followers.push(currentUserUid);
        // refUser.update({"followers":followers});

        // When user follows a new user 
        // it should take followed users old posts to their post pool.
        // First we have to get the followed user posts.
        loadOldPosts(currentUserUid,followedUid);   // if it is unfollow operation. Don't loadOldPosts.

        admin.database().ref("/Users").child(currentUserUid).once("value",(data)=>{
            const val = data.val();
            const fullname = val.firstName + " "+ val.lastName;
            sendNotification(followedUid,"Yeni bir takipiçiniz var.",fullname+" sizi takip etmeye başladı.");
        });
        // Control if any issue or not !
        // sendNotification(followedUid,"Yeni bir takipçiniz var.","Yeni bir takipçiniz var.");

    });


// Create a new function for message isRead situation...
// When a message read.. Make its situation isRead = true for all chatrooms.
exports.readMessageListener = functions.database.ref("Messages/{messageUUID}")
    .onUpdate((snap,context) =>{
        // i can only update mychatroom... because we are not storing sender information...
        const receiverUUID = context.auth.uid;
        const messageUUID = context.params.messageUUID; // get message id

        const senderUUID = snap.after.val().senderUUID;


        const refChatRoom = admin.database().ref("/ChatRooms");
        // update sender chatroom
        refChatRoom.child(senderUUID).child(receiverUUID).child("messages").child(messageUUID).update({'read':true});
        // update receiver chatroom
        refChatRoom.child(receiverUUID).child(senderUUID).child("messages").child(messageUUID).update({'read':true});

    });


/**
 * send notifications, when someone liked your post or makes comment to your post.
 * 
 * 
 */
exports.postLikeListener = functions.database.ref("Posts/{postUUID}/likers/{likerUUID}")
    .onCreate((snap,context) =>{
        const postUUID = context.params.postUUID;
        const likerUUID = context.params.likerUUID;

        admin.database().ref("/Posts").child(postUUID).once("value",(data)=>{
            const val = data.val();
            const userUUID = val.userUUID;
            admin.database().ref("/Users").child(userUUID).once("value",(dat)=>{
                const v = dat.val();
                const fullname = v.firstName + " "+v.lastName;
                sendNotification(userUUID,"Anipal",fullname+" paylaştığınız bir gönderiyi beğendi.");
            });
        });
    });

exports.postCommentListener = functions.database.ref("Posts/{postUUID}/comments/{commentUUID}")
    .onCreate((snap,context)=>{
        const commentValue = snap.val();
        
        // get the post uidp
        const postUUID = context.params.postUUID;
        // find the post owner
        admin.database().ref("/Posts").child(postUUID).once("value",(data)=>{
            const val = data.val();
            const userUUID = val.userUUID;
            const fullname = commentValue.senderName;
            sendNotification(userUUID,fullname+" paylaştığınız bir gönderiye yorum yaptı.",commentValue.comment);
        });

        // const fullname = commentValue.senderName;
        // sendNotification(userUUID,fullname +" paylaştığınız bir gönderiye yorum yaptı.",commentValue.comment);
    });

// exports.postDonationListener = functions.database.ref("Posts/{postUUID}/donators")
//     .onWrite((snap,context) =>{
//         const donatorUUID = context.auth.uid;
//         const postUUID = context.params.postUUID;
//         const donationVal = snap.after.val();

//         console.log("Donation Val : ",donationVal);
        
//         admin.database().ref("/Posts").child(postUUID).once("value",(data)=>{
//             // find the user who creates the post
//             const postVal = data.val();
//             const userUUID =  postVal.userUUID;
//             // const purpose = postVal.donationPurpose;
//             admin.database().ref("/Users").child(donatorUUID).once("value",(data)=>{
//                 const val = data.val();
//                 const fullname = val.firstName + " "+val.lastName;
//                 sendNotification(userUUID,"Bağış Barı",fullname+" bağış barınıza "+donationVal[donatorUUID]+" pati katkıda bulundu.");
//             });
//         });

//     });


exports.makeDonationListener = functions.database.ref("/Users/{userUUID}/donations/{donationUUID}")
    .onWrite((snap,context)=>{
        
        const beforeDonationVal = snap.before.val();
        const donationVal = snap.after.val();
        const donatorUUID = context.params.userUUID;
        const donationPostUUID = context.params.donationUUID;

        console.log("Before Donation Value : ",beforeDonationVal);
        console.log("Donation Value : ",donationVal);
        console.log("Donator UUID : ",donatorUUID);
        console.log("Donation Post UUID : ",donationPostUUID);
        
        const donationPrice = donationVal;
/**
         * after you find the donation post
         * get the currentDonation and your donation quantity. if they are not greater than the 
         * donation price. Make the donation otherwise don't make the donation. And by the way.
         * if donation is cancelled. Give users money back. Otherwise make the donation and update post from here.
         * when you make the donation increase currentDonationPrice and add yourself into donators list on the post.
         * If you are still a donator then update your donation. 
         */

        admin.database().ref("/Posts").child(donationPostUUID).once("value",(data)=>{
            const val = data.val();
            const currentPrice = val.currentDonation;
            const maxPrice = val.donationPrice;
            const postOwnerUUID = val.userUUID;

            // console.log("Value : ",val);
            console.log({'Current Price : ':currentPrice,'Max Price : ':maxPrice, "Donation Price : ":donationPrice});

            if((currentPrice+donationPrice)>maxPrice){
                // delete donation from users donation
                refundUsersMoneyDeleteDonation(donationPostUUID,donatorUUID);
                return;
            }
            
            // Otherwise if we can make the donation
            // Complete all actions
            // Current donation, donators.
            val.currentDonation = currentPrice + donationPrice;
            // if it exists
            if(val.donators===undefined) {
                val['donators']={};  
                val.donators[donatorUUID] = donationPrice;
            }
            else val.donators[donatorUUID] = donationPrice+beforeDonationVal;
            // then update post again.
            admin.database().ref("/Posts").child(donationPostUUID).set(val); 
            // also send notification.
            admin.database().ref("/Users").child(donatorUUID).once("value",(data)=>{
                const v = data.val();
                console.log(v);
                const fullname = v.firstName + " "+v.lastName;
                const str = fullname+" bağış barınıza "+donationPrice+" pati katkıda bulundu.";
                console.log("Notification : ",str);
                console.log("Post Owner : ",postOwnerUUID);
                sendNotification(postOwnerUUID,"Bağış Barı",str);
            });
        });
        
    });

exports.listenStorage = functions.storage.object().onFinalize(async (object)=>{
    console.log(object);
});