import os
import time
import shutil
import datetime
from glob import glob

zip_path = "C:\\Program Files\\7-Zip\\7z.exe"


def filename2timestamp(filename: str) -> float:
    filename = filename.removesuffix('.7z')
    return time.mktime(datetime.datetime.strptime(filename, "%d.%m.%Y_%H.%M").timetuple())

def filename2iso8601(filename: str) -> str:
    filename = filename.removesuffix('.7z')
    return datetime.datetime.strptime(filename, "%d.%m.%Y_%H.%M").replace(microsecond=0).isoformat()

def main():
    lista = glob("*.7z")
    lista.sort(key=filename2timestamp)
    
    print(os.getcwd())

    os.system("rmdir /S /Q tmp")
    os.system("git clone https://github.com/davide99/fin.git tmp")
    os.chdir("tmp")

    for file in lista:
        for a in glob("*"):
            if os.path.isdir(a):
                shutil.rmtree(a)
            else:
                os.remove(a)

        os.system("\"" + zip_path + "\" x ..\\" + file + " -o.")
        os.system("git add --all")
        os.environ["GIT_COMMITTER_DATE"] = filename2iso8601(file)
        os.system("git commit -m \"Commit " + filename2iso8601(file) + "\"")

if __name__ == "__main__":
    main()
