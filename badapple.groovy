@Grab(group='commons-io', module='commons-io', version='2.11.0')

ASCII_CHARS = ' .:-=+*#%@'
WIDTH = 80
HEIGHT = 40

def downloadVideo(url) {
    println "Downloading video..."
    "yt-dlp -f worst -o video.mp4 ${url}".execute().waitFor()
}

def rgbToAscii(r, g, b) {
    def brightness = (r + g + b) / 3
    def index = (int)(brightness * (ASCII_CHARS.length() - 1) / 255)
    ASCII_CHARS[index]
}

def extractAndDisplayFrame(time) {
    def cmd = "ffmpeg -ss ${time} -i video.mp4 -vframes 1 -vf scale=${WIDTH}:${HEIGHT} -f rawvideo -pix_fmt rgb24 - 2>/dev/null"
    def proc = cmd.execute()
    def pixels = proc.inputStream.bytes
    
    if (pixels.length > 0) {
        print "\033[2J\033[H"
        
        for (y in 0..<HEIGHT) {
            for (x in 0..<WIDTH) {
                def idx = (y * WIDTH + x) * 3
                if (idx + 2 < pixels.length) {
                    def r = pixels[idx] & 0xFF
                    def g = pixels[idx + 1] & 0xFF
                    def b = pixels[idx + 2] & 0xFF
                    print rgbToAscii(r, g, b)
                }
            }
            println()
        }
    }
}

def url = args ? args[0] : 'https://youtu.be/FtutLA63Cp8'
downloadVideo(url)

def fps = 10
def duration = 30
def time = 0

while (time < duration) {
    extractAndDisplayFrame(time)
    sleep((1000 / fps) as long)
    time += 1.0 / fps
}
