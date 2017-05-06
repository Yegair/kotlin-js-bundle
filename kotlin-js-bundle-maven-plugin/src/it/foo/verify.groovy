def artifactId = "foo"

def script = new File((File)basedir, "src/main/resources/foo.js").text
def bundle = new File((File)basedir, "target/js/${artifactId}.bundle.js").text

return bundle == script