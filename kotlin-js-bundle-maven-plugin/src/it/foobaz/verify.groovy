def artifactId = "foobaz"

def foo = new File((File) basedir, "../foo/src/main/resources/script.js").text
def baz = new File((File) basedir, "../baz/src/main/resources/script.js").text
def script = new File((File) basedir, "src/main/resources/script.js").text
def bundle = new File((File) basedir, "target/js/${artifactId}.bundle.js").text

def fooIndex = bundle.indexOf(foo)
def bazIndex = bundle.indexOf(baz)
def scriptIndex = bundle.indexOf(script)

return fooIndex < scriptIndex && bazIndex < scriptIndex