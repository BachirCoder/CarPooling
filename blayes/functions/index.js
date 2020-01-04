'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const bookN = require('./notifications/bookNotification.js');
const cancelN = require('./notifications/cancelNotification.js');
const cancel2N = require('./notifications/cancel2Notification.js');
const confirmN = require('./notifications/confirmNotification.js');
const rejectN = require('./notifications/rejectNotification.js');
const reject2N = require('./notifications/reject2Notification.js');
//var temp = new bookN();
//let val1 = bookN.bookNotification();

//another.data.bookN.bookNotification();

exports.bookNotification = bookN;
exports.cancelNotification = cancelN;
exports.cancel2Notification = cancel2N;
exports.confirmNotification = confirmN;
exports.rejectNotification = rejectN;
exports.reject2Notification = reject2N;
//let val2 = cancelN.bookNotification();
//let val3 = confirmN.bookNotification();
//let val4 = rejectN.bookNotification();
