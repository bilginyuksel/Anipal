const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

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

// when you follow someone you have to update your post status
// means the users post session has to fill older posts of other user.
// exports.loadOldPostsWhenYouFollow = functions.database.ref('Users/{userID}/following/{followedUID}')
//     .onWrite((snapshot,context) => {
//         // this function should work when you follow someone
//         // You will get the usersID
//         // And find his all posts. Load to your userpost field
//         const authUserUid = context.auth.uid;
//         const followedUserUid = context.params.followedUID; // maybe that is not a true usage
//         const oldPostsRef = admin.database().ref("/UserPersonalPosts").child(followedUserUid);
//         console.log(snapshot.after.val());

//         oldPostsRef.once("value",(data)=>{
//             const oldPosts = data.val();

//             console.log(oldPosts);
//             if(oldPosts){
//                 // If there is any post exists
//                 const ref = admin.database().ref("/UserPosts");
//                 oldPosts.forEach(element=>{
//                     ref.child(authUserUid).child(element.postUUID).set(element);
//                 });
//             }
//         });

//     });


exports.createChatRoom = functions.database.ref("Messages/{messageUUID}")
    .onWrite((snap,context) => {
        // When a new message created !

        /* 
        also update chatrooms */
        const messageData = snap.after.val();
        // find sender and receiver
        const receiver = messageData.receiverUUID
        const sender = context.auth.uid;

        const refChatRoom = admin.database().ref("/ChatRooms");
        // sender's chatroom
        refChatRoom.child(sender).child(receiver).child("messages").child(messageData.messageUUID).set(messageData);
        refChatRoom.child(sender).child(receiver).update({"lastMessage":messageData.message,"lastMessageDate":messageData.sendDate});
        // receiver's chatroom
        refChatRoom.child(receiver).child(sender).child("messages").child(messageData.messageUUID).set(messageData);
        refChatRoom.child(receiver).child(sender).update({"lastMessage":messageData.message,"lastMessageDate":messageData.sendDate});
        /*
        send notification to receiver
        notification stuff here */

    });