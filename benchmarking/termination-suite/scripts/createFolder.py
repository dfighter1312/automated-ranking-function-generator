import os
import shutil
from pathlib import Path

files = [f for f in os.listdir('.') if os.path.isfile(f)]
from zipfile import ZipFile,BadZipFile

def remove(path):
    """ param <path> could either be relative or absolute. """
    if os.path.isfile(path) or os.path.islink(path):
        os.remove(path)  # remove the file
    elif os.path.isdir(path):
        shutil.rmtree(path, ignore_errors=False)  # remove dir and all contains
    else:
        raise ValueError("file {} is not a file or dir.".format(path))


MAKEFILE = "all:\n\t${JAVAC} -g *.java\n\tjar cfm {}.jar MANIFEST.MF .\n\nclean:\n\trm -rf *.class\n\trm -rf *.jar\n"

MANIFEST ="Manifest-Version: 1.0\nMain-Class: {}\n"


for file in files:
    try:
        with ZipFile(file, 'r') as zipObj:
            zipObj.extractall(file[0:-4])
    except BadZipFile as e:
        print("Not a zip file:" + file)
        continue
    with ZipFile("./" + file[0:-4] + "/source.zip", 'r') as zipObj:
        zipObj.extractall("./" + file[0:-4])
    
    sub_fold_file = [f for f in os.listdir('./' + file[0:-4]) if not f.endswith(".java")]

    for x in sub_fold_file:
        # os.remove('./' + file[0:-4] + "/" +x)
        remove('./' + file[0:-4] + "/" + x)

    with open('./' + file[0:-4] + "/" + "Makefile", 'w+') as f:
        f.write(MAKEFILE.format(file[0:-4]))
    
    with open('./' + file[0:-4] + "/" + "MANIFEST.MF", 'w+') as f:
        f.write(MANIFEST.format(file[0:-4]))


    new = Path("./tmp/" + file[0:-4])
    new.parent.mkdir(parents=True, exist_ok=True)
    Path("./" + file[0:-4]).rename(new)
