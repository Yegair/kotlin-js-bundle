def artifactId = "foobar"

def foo = new File((File) basedir, "../foo/src/main/resources/script.js").text
def bar = new File((File) basedir, "../bar/src/main/resources/script.js").text
def script = new File((File) basedir, "src/main/resources/script.js").text
def bundle = new File((File) basedir, "target/js/${artifactId}.bundle.js").text

def fooIndex = bundle.indexOf(foo)
def barIndex = bundle.indexOf(bar)
def scriptIndex = bundle.indexOf(script)

return fooIndex < scriptIndex && barIndex < scriptIndex