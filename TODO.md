* Stop using ObjectStreams and just send bytes, there's garbage in the stream that makes things messy.
* The alternative is using a separate data stream.
* Split pom.xml in multiple modules or whatever, to allow multiple jar targets (each controller class should be a separate target).
