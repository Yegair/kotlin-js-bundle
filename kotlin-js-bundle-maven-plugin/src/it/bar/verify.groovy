def artifactId = "bar"

def script = new File((File)basedir, "src/main/resources/bar.js").text
def bundle = new File((File)basedir, "target/classes/${artifactId}.bundle.js").text

return bundle == script