def artifactId = "foobar"

def foo = new File((File) basedir, "../foo/src/main/resources/foo.js").text
def bar = new File((File) basedir, "../bar/src/main/resources/bar.js").text
def script = new File((File) basedir, "src/main/resources/foobar.js").text
def bundle = new File((File) basedir, "target/classes/${artifactId}.bundle.js").text

def fooIndex = bundle.indexOf(foo)
def barIndex = bundle.indexOf(bar)
def scriptIndex = bundle.indexOf(script)

return fooIndex < scriptIndex && barIndex < scriptIndex