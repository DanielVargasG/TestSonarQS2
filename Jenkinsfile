node{
def mvnHome
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      git branch: 'feature/Jenkins', credentialsId: 'a0de979a-fb3a-40bc-b074-16981d877dfb', url: 'https://github.com/equipe2nuit/docgenerator'
      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured
      // **       in the global configuration.           
     mvnHome = tool 'M3'
   }
  stage ('Build') {
 
      bat "mvn clean"
 
    } // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe reports and FindBugs reports
    // stage ('test') {
 
      //bat "mvn install"
 
   // } // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe reports and FindBugs reports
      

 }
