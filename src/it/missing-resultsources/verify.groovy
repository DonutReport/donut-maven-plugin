def buildLog = new File(basedir, 'build.log')
text = buildLog.text

assert text.contains('Required parameter `resultSources` not set.')
