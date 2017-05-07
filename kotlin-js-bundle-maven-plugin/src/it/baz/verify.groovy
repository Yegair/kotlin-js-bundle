def artifactId = "baz"

def script = new File((File)basedir, "src/main/resources/baz.js").text
def bundle = new File((File)basedir, "target/classes/${artifactId}.bundle.js").text

return bundle == script