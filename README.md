#cardwire

Display module that sources string data from arrays - 1st Commit

Added Card classes and custom ArrayList adapter. Serialize/deserialize methods to create and receive Ndef messages. NFC class expanded to support Card object sending and receiving. (Not tested) - 2nd Commit

Problem sending Card object through Ndef format. Reverted to basic Card object displaying and NFC initialization. - 3rd Commit

Sends Card as parseable String through Ndef format. Able to send/receive Strings and use these to create Card objects. Next step: display Card objects in ListView. - 4th Commit

Created activity to edit and update user's Card by writing and reading file. Next up: dynamic resizing of user's card (Add/Delete). - 6th Commit

Added PIN saving/reading methods via SharedPreferences and adding other users by PIN activity. Next: use web sockets to send and receive PIN add requests and responses. Create default JSON objects for various server-based actions. - 8th Commit 
