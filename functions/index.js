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
            title:'Bir mesajınız var',
            body:message
        }
    };

    admin.database().ref("/Users").child(userUid).child("messageToken").once("value",data=>{
        const messageToken = data.val();
        admin.messaging().sendToDevice(messageToken,payload);
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
        sendNotification(receiver,"Bir mesajınız var.",messageData.message);

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

        // Control if any issue or not !
        sendNotification(followedUid,"Yeni bir takipçiniz var.","Yeni bir takipçiniz var.");

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
 */
exports.postLikeListener = functions.database.ref("Posts/{postUUID}/likers/{likerUUID}")
    .onCreate((snap,context) =>{
        const ownerUUID = context.params.userUUID;
        const likerUUID = context.params.likerUUID;

        sendNotification(ownerUUID,"Gönderi","Paylaştığınız gönderi beğenildi.");
    });

exports.postCommentListener = functions.database.ref("Posts/{postUUID}/comments/{commentUUID}")
    .onCreate((snap,context)=>{
        const commentValue = snap.val();
        // get the post uidp
        const postUUID = context.params.postUUID;
        // find the post owner
        

    });