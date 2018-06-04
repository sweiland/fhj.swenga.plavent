# fhj.swenga2017.plavent

PlaVent is an event planner application and was developed during the study course Software Engineering Advanced at FH JOANNEUM (UAS)
in the Bachelor's degree Informationsmanagement (IMA16). The project was supported by *DI (FH) Johann Blauensteiner* and *DI (FH) Stefan Krausler-Baumann*.

Team Members:
- Alexander Hoedl
- Gregor Fernbach
- Sebastian Weiland
- Stefan Heider



## Setup Guide
1. Clone project source from [here](https://github.com/sweiland/fhj.swenga2017.plavent)
2. Create ``New dynamic Web project`` eclipse project, convert to maven project and import sources  
3. Setup ``app.properties`` properties (db-connection-information and mail-information)
4. Change jpa-properties in dispatcher-servlet.xml to required attributes (``validate``, ``update``, or ``create-drop``)
5. Publish project to Tomcat and start Tomcat (8.5)
6. Open Web application [here](http://localhost:8080/fhj.swenga2017.plavent/)
7. If there is a new database behind (or started via create-update) a setup wizard is shown instead of the login page. Click on Button ``Start initial setup` to create the database environment.
8. Login credentials: admin/password, host/password, guest/password
9. Welcome to PlaVent - Check out the Happening Management Tutorial (Menu ``Happening Management``)