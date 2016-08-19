After git clone, go to build.gradle under app directory and find the following section
 buildTypes.each {
        it.buildConfigField 'String', 'TMDB_API_KEY', "\"[API KEY]\"";
    }

Place API key inside the placeholder position in the build.gradle