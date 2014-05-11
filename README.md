yet-another-task-tracker
========================

The final project of clojurecourse.by. Represents a simple task tracker with user management, task controlling an so on.
A website is written with Enfocus in ClojureScript and Clojure itself.

There are the following features (at this stage):
 - User authentication via EDN format with encrypting the password and sending to the server;
 - Page protection (/tasks);
 - Working through enfocus templates;

Coming:
 - Connecting with the DB (possibly mongodb)
 - Prototype of the application itself

The deployed application on http://madcatsam.herokuapp.com/

login/pass:
admin/s

## Usage

```bash
lein cljsbuild once
lein ring server
```

## License

Copyright (C) 2014

Distributed under the Eclipse Public License, the same as Clojure.
