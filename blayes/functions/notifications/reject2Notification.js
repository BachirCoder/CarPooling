'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');


var var11 = functions.database.ref('/notifications/rejectConfirmation').onWrite(event => {
  if (!event.data.val()) {
              console.log("Not a new write event");
              return;
          }
  // Get the list of device notification tokens.
  const getDeviceTokensPromise = admin.database().ref(`/notifications/rejectConfirmation/token`).once('value');
  const postId = admin.database().ref(`/notifications/rejectConfirmation/post`).once('value');
  return Promise.all([getDeviceTokensPromise, postId]).then(results => {
    const tokensSnapshot = results[0];
    const varr = results[1];
    const postId = varr.val();
    console.log('Post ID is: ', postId);


    console.log('Token is', tokensSnapshot.val());

    // Notification details.
    const payload = {
      data:{
      "title": "reject confirmation",
      "post": postId,
    }
    };

    // Listing all tokens.
    const token = tokensSnapshot.val();

    // Send notifications to all tokens.
    return admin.messaging().sendToDevice(token, payload).then(response => {
      // For each message check if there was an error.
      const tokensToRemove = [];
      response.results.forEach((result, index) => {
        const error = result.error;
        if (error) {
          console.error('Failure sending data to', token[index], error);

        }
        else console.log('No error sending data.');
      });

      const payload = {
        notification: {
          title: 'Réservation annulée.',
          body: `Votre chauffeur a annulé votre réservation. Cliquez ici pour vérifier.`,
          sound: 'default',
          click_action: 'OPEN_NOTIFICATION',
          }
      };

      return admin.messaging().sendToDevice(token, payload).then(response => {
        // For each message check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            console.error('Failure sending notification to', token[index], error);

          }
          else console.log('No error sending notification.');
        });

      return Promise.all(tokensToRemove);
    });
  });
});
});


module.exports = var11;
