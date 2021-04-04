# Warsmash: Mod Engine
This is a Warcraft III modding project that aspires to eventually make Warcraft III modding easier by providing a replacement component for the game program that can be more easily modified while still requiring the Blizzard Warcraft III game and assets to be installed on the computer only from Blizzard. In short, buy Warcraft III from Blizzard, and then you can begin to use this mod engine with it.

## How to Install
1. Clone the repo
2. Open the repo as a Gradle Project with your Java IDE of choice (IntelliJ seems to be the easiest to get working)
3. Run the Gradle target called "run"

## How to Build
1. Clone the repo
2. Run ./gradlew desktop:dist
3. Find "./desktop/build/libs/desktop-1.0.jar"
4. Open this JAR with 7zip
5. Remove the duplicate "META-INF/services/javax.imageio.spi.ImageReaderSpi" files that share the same name located in META-INF so that only the BLP related file is present
6. Save the JAR and exit 7zip
*(This process will hopefully become easier in the future)*

## Background and History
My current codebase is running on Java 8. It contains:
- A transcription of only the relevant portions of this WebGL viewer necessary to load MDX models and W3X map data, and to display the models https://github.com/flowtsohg/mdx-m3-viewer
- A transcription of the HiveWE terrain rendering systems from this project, then modified to interface nicely with the ported viewers MDX rendering https://github.com/stijnherfst/HiveWE
- The shaders GLSL code from both of the above projects, originally copied in almost their exact form but with a few necessary changes. As a result, there are still bizarre disparities in the GL Shader Language version used for different things within this same project
- Graphical Enhancements to viewer from this fork of it: https://github.com/d07RiV/wc3data
(By copying the above repo, I was able to add support for water waves, shadow on the edge of the map, shadow under buildings, UberSplat under buildings, the little shadow picture under units, and some earlier drafts of the unit selection mouse intersect code that I mostly replaced at this point. I had to heavily modify the Splat logic to make them mobile as this repo at the time of copying was only able to create the shadow in a static location and upload the single location to the graphics card. Theoretically I have found cases for Ramps in map terrain that are handled properly by this repo and not by HiveWE terrain rendering nor by my copy of it, but I never prioritized investigating those ramp problems further)
- I use Java-based blp-iio-plugin so I didnt need to bother porting the BLP parser from the model viewer BUT this one I am using has some issue on campaign art textures where the alpha isnt loaded, so NightElfCampaign3D_exp for example has render artifacts around palm leaves https://github.com/DrSuperGood/blp-iio-plugin
- I use an MPQ parser by the same guy as the above, DrSuperGood, but he didnt put it on github so I just included the sourcecode in the repo for now
- For loading Unit Data, I have two SLK parsers and two INI parsers which is gross. One set is copied from ReteraModelStudio and the other is transcribed from viewer. I am mostly using the ReteraModelStudio one where possible because I wrote it not as a copy of anything else from my own intuition years ago and the API interfaces better with my high level unit data API that I copied from ReteraModelStudio (https://github.com/Retera/ReterasModelStudio)
- For loading map changeset unit data (Object Editor) I am using a transcription of the parsers written by PitzerMike for Grimoire/Widgetizer. Somebody linked me some ancient pastebin on the Hive at some point that included this C++ code so I wrote a Java port in my model editor repo and copied it to this repo and cleaned it up a bit
- For loading TGA image files such as building pathing, I use the same TGA parser included in ReteraModelStudio codebase. It was written by OgerLord long ago https://github.com/OgerLord/WcDataLibrary/blob/master/src/de/wc3data/image/TgaFile.java
