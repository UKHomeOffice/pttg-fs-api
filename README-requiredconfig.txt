You will need a settings.xml file in ~/.m2 containing the following xml with your own artifactory username and api key.

1 - Go to artifactory and login (https://artifactory.digital.homeoffice.gov.uk/artifactory/webapp/#/login)
2 - Click Sign in with HOD SSO
3 - Click your user name to access the profile (https://artifactory.digital.homeoffice.gov.uk/artifactory/webapp/#/profile)
4 - Click the cog to generate an api key
5 - Copy the api key and create the settings.xml file as below


<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <servers>
    <server>
      <id>snapshots</id>
      <username>most-likely-your-HOD-email-address</username>
      <password>the-api-key-from-artifactory</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <repositories>
        <repository>
          <id>snapshots</id>
	      <url>https://artifactory.digital.homeoffice.gov.uk/artifactory/libs-snapshot-local</url>
        </repository>
      </repositories>
    </profile>
  </profiles>

</settings>
