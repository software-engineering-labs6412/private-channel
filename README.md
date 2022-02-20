# private-channel
## Installation notes
### Postgres installation
1) Install docker-desktop: https://www.docker.com/products/docker-desktop;
**NOTE**: With docker-desktop **WSL2 component** will be installed;
2) Sign in to docker account:
    * **Login**: software.engineering.labs@mail.ru
    * **Password**: ZFzBOIzgZEcVTuTsSAdHJqmMeSPcNDBRjhuACDkS
3) Start docker;
    * Sometimes there may be a problem related to the lack of updates to the Linux kernel. You can download the update here:
    https://docs.microsoft.com/ru-ru/windows/wsl/install-manual#step-4---download-the-linux-kernel-update-package
4) Create JSON file with settings for deploying postgres instance on docker:
#### Example
```json
{
	"instanceName" : "pg-se",
	"user" : "postgres",
	"password" : "postgres",
	"db" : "postgresDev",
	"port" : "5432"
}
```
5) Open `installation` folder and run cmd;
6) In console run next command: `java -jar db_installation-1.0.jar -settingsFile=<file_path>` 
**NOTE**: java version must be **15 and above**. **Tool repo**: https://github.com/software-engineering-labs6412/db_installation;
7) Please wait for the container to set up;
8) Now you have pg databases server with IP = `127.0.0.1:<specified port>`