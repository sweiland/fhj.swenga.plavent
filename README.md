# fhj.swenga2017.plavent
PlaVent is an event planner application and was developed during the study course Software Engineering Advanced at FH JOANNEUM (UAS)
in the Bachelor's degree Informationsmanagement (IMA16). The project was supported by *DI (FH) Johann Blauensteiner* and *DI (FH) Stefan Krausler-Baumann*.

Team Members:
- Alexander Hoedl
- Gregor Fernbach
- Sebastian Weiland
- Stefan Heider

Workload distribution:
- Alexander Hoedl: Project manager and responsible for Happening Management
- Gregor Fernbach: Responsible for User management and security
- Sebastian Weiland: Responsible for Design(Bootstrap,Thymeleaf, CoreUI) and Dashboard
- Stefan Heider: Responsible for Happening Category Management and testing 


## Setup Guide
1. Download project source from [here](https://github.com/sweiland/fhj.swenga2017.plavent/releases/tag/v1.0.7)
2. Create ``New dynamic Web project`` eclipse project, convert to maven project and import sources  
3. Setup ``app.properties`` in folder ``src`` (db-connection-information and mail-information) based on [this](https://gist.github.com/sweiland/d6fed135f0b072ac6afc12ec00cc81d0).
4. Change jpa-properties in dispatcher-servlet.xml to required attributes (``validate``, ``update``, or ``create-drop``)
5. Setup your eclipse project (Server, Runtime, ...)
6. Publish project to Tomcat and start Tomcat (8.5)
7. Open Web application [here](http://localhost:8080/fhj.swenga2017.plavent/)
8. If there is a new database behind (or started via create-update) a setup wizard is shown instead of the login page. Click on Button ``Start initial setup`` to create the database environment.
9. Login credentials: admin/password, host/password, guest/password
10. Welcome to PlaVent - Check out the Happening Management Tutorial (Menu ``Happening Management``)
