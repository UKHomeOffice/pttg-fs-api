Some config is required in order to retrieve dependencies from Artifactory for the bank integration jars.
The alternative is to clone the other projects (pttg-fs-integration from GitHub and pttg-fs-barclays from GitLab), build
them, and publish the jars to your local maven repository (gradle publishToMavenLocal).


You will need a settings.xml file in ~/.m2 containing the following xml with your own artifactory username and password.

Obtain an artifactory username and password:
1 - Go to the artifactory login (https://artifactory.digital.homeoffice.gov.uk/artifactory/webapp/#/login)
2 - Click Sign in with HOD SSO
3 - Ask someone (eg jaykeshur) on DSP slack to reset your password
4 - Log in to artifactory again and reset your password to one of your choosing
5 - Now you can use that password in your settings.xml
(or get an encrypted version of it from your artifactory user profile page)

<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <servers>
    <server>
      <id>snapshots</id>
      <username>most-likely-your-HOD-email-address</username>
      <password>some-password</password>
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
