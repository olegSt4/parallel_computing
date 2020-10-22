parallel_computing
Programm builds simple inverted index of .txt files in single-thread mode or in multi-thread mode (to 100 threads)

## Prerequisites
Instaled JDK 8 and JRE 8

Instaled IntelliJ IDEA ver. 2018.3.3 or higher (optional)

## Instalation and running
**1] First way: command line**
1) Clone this repository
2) In project folder run next command:
```
javac -sourcepath ./src -d bin src/my/work/Main.java
```
3) Upload folder(s) with your .txt files to source folder (with name **files**) or leave the folders that are already there (like example) 
4) Then run command: 
```
java -classpath ./bin my.work.Main
```
5) Follow the instructions

**2] Second way: IntelliJ IDEA**
1) Create new Java project
2) Add two new classes: Main and IndexBuilder. Copy the corresponding code in them from this repository (*src/my/work*)
3) Create new folder in the root of the project with name *files*
4) Upload folder(s) with your .txt files into folders *files*
5) Build the project (Build -> Build Project)
6) Run the project
7) Follow the instructions

## Usage
For correct working, input files must be collected in a folder(s) and placed in source folder with name *files*

## Contacts
Oleh Stefura. Group DA71
