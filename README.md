#cardwire
Display module that sources string data from arrays - Jan. 19

Added Card classes and custom ArrayList adapter. Serialize/deserialize methods to create and receive Ndef messages. NFC class expanded to support Card object sending and receiving. (Not tested) - Jan. 22

Problem sending Card object through Ndef format. Reverted to basic Card object displaying and NFC initialization. - Jan. 24

Sends Card as parseable String through Ndef format. Able to send/receive Strings and use these to create Card objects. - Jan. 24
