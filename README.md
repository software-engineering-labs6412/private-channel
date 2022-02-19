# private-channel
## Installation notes
### Postgres installation
1) Install docker-desktop: https://www.docker.com/products/docker-desktop;
**NOTE**: With docker-desktop **WSL2 component** will be installed;
2) Sign in to docker account:
    * **Login**: software.engineering.labs@mail.ru
    * **Password**: ZFzBOIzgZEcVTuTsSAdHJqmMeSPcNDBRjhuACDkS
3) Create JSON file with settings for deploying postgres instance on docker
#### Example
```json
{
	"instanceName" : "pg-se",
	"user" : "postgres",
	"password" : "postgres",
	"db" : "postgresDev",
	"port" : "7432"
}
```
4) Open `installation` folder and run cmd;
5) In console run next command: `java -jar db_installation-1.0.jar -settingsFile=<file_path>` 
**NOTE**: java version must be **15 and above**
6) Please wait for the container to set up;
7) Now you have pg database instance with IP = `127.0.0.1:<specified port>`