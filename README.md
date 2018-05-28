# fhj.swenga2017.plavent

PlaVent is an event planner application.


This application was developed during the study course Software Engineering Advanced at FH JOANNEUM (UAS)
in the Bachelor's degree Informationsmanagement (IMA16).

Team Members:

Alexander Hödl
Gregor Fernbach
Sebastian Weiland
Stefan Heider

## Setup Guide
1. Download project from [here](https://github.com/sweiland/fhj.swenga2017.plavent/releases/tag/v0.0.1a2)
2. Setup ``db.properties`` (db-connection-information)
3. Change jpa-properties in dispatcher-servlet.xml to required attributes (``validate``, ``update``, or ``create-drop``)
4. Publish to and start Tomcat (8.5)
5. Access application from [here](http://localhost:8080/fhj.swenga2017.plavent/)
6. Start initial setup (if necessary)
7. Login credentials: admin/password, host/password, guest/password
8. See Happening Management for Tutorial Happening
