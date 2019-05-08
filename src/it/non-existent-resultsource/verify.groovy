def buildLog = new File(basedir, 'build.log')
text = buildLog.text

assert text.contains('The source directory does not exist')
