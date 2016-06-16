
#mvn install:install-file -Dfile=lib/lucene-analyzers-kuromoji-5.0.0-SNAPSHOT.jar -DgroupId=bitter_fox.line.internship -DartifactId=lucene-analyzers-kuromoji-neologd -Dversion=5.0.0 -Dpackaging=jar -DgeneratePom=true

if [ ! -e lib/kuromoji-ipadic-neologd-0.9.0-20160613.jar ]; then
    wget https://github.com/kazuhira-r/kuromoji-with-mecab-neologd-buildscript/blob/master/build-atilika-kuromoji-with-mecab-ipadic-neologd.sh -P /tmp
    chmod a+x /tmp/build-atilika-kuromoji-with-mecab-ipadic-neologd.sh
    /tmp/build-atilika-kuromoji-with-mecab-ipadic-neologd.sh
    cp /tmp/kuromoji-ipadic-neologd-0.9.0-20160613.jar lib
fi


mvn install:install-file -Dfile=lib/kuromoji-ipadic-neologd-0.9.0-20160613.jar -DgroupId=bitter_fox.line.internship -DartifactId=kuromoji-ipadic-neologd -Dversion=0.9.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=lib/kuromoji-core-0.9.0.jar -DgroupId=bitter_fox.line.internship -DartifactId=kuromoji-core -Dversion=0.9.0 -Dpackaging=jar -DgeneratePom=true
