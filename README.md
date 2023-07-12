# Gitlet

A version-control system that implements a subset of basic features of Git.

## **Catalog**

[Usage](#usage)

[Commands](#commands)

[Design](#design)

[Reference](#reference)

## Usage

The basic usage is `java gitlet.Main <COMMAND> <OPERAND1> <OPERAND2> ...`.

```shell
# Compile
cd gitlet/
javac *.java
# Example:
# add a file "new.txt" in cwd, then
java gitlet.Main add new.txt
java gitlet.Main commit "add new.txt"
```

## Commands

A concise description of all commands available in Gitlet is as follows:

### init

***Usage*:** `java gitlet.Main init`

Creates a version-control folder `.gitlet` in the current working directory.

### add

***Usage*:** `java gitlet.Main add [file name]`

Adds a copy of the file as it currently exists to the staging area.

***Differences from Git*:** 

In Gitlet, only one file may be added at a time.

### commit

***Usage*:** `java gitlet.Main commit [message]`

Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time, creating a new commit.

***Differences from Git*:** 

In Gitlet, commits carry less metadata compared with real git.

### rm

***Usage*:** `java gitlet.Main rm [file name]`

Removes a file from the current working directory.

### log

***Usage*:** `java gitlet.Main log`

Displays all commit information starting at HEAD in reverse chronological order.

Similar to `git log --first-parent`.

### global-log

***Usage*:** `java gitlet.Main global-log`

Lists all commits ever made.

### find

***Usage*:** `java gitlet.Main find [commit message]`

Prints out the ids of all commits that have the given commit message, one per line.

***Differences from Git*:** Doesn’t exist in real git.

### status

***Usage*:** `java gitlet.Main status`

Displays what branches currently exist, and marks the current branch with a *. Also displays what files have been staged for addition or removal.

### checkout

***Usages*:**

1. `java gitlet.Main checkout -- [file name]`
2. `java gitlet.Main checkout [commit id] -- [file name]`
3. `java gitlet.Main checkout [branch name]`

Depending on the given args, checks out a file in HEAD commit, a file specified by commit id, or all files in the head of a specified branch.

***Differences from Git*:** 

Real git does not clear the staging area and stages the file that is checked out. 

### branch

***Usage*:** `java gitlet.Main branch [branch name]`

Creates a new branch with the given name, and points it at the current head commit.

### reset

***Usage*:** `java gitlet.Main reset [commit id]`

Checks out all the files tracked by the given commit.

***Differences from Git*:** 

This command is closest to using the `--hard` option, as in `git reset --hard [commit id]`.

### merge

***Usage*:** `java gitlet.Main merge [branch name]`

Merges files from the given branch into the current branch.

***Differences from real git*:** 

Real Git will force the user to resolve the merge conflicts before committing to complete the merge. 

Gitlet just commits the merge, conflicts and all.

## Design

### Abstraction Principle

- An issue with version control systems:  

  Requires cumbersome operations like hashing, serialization, map operations, directory concatenation, file I/O, etc.

- Solution:

  - On a higher level, involve only communications between objects (between Blob and Commit, there should only be `Blob b = commit1.get(filename)`)
  
  - Eliminate the need to dive into low-level operations through encapsulation.
    
    i.e. Outside the class of that object, never try to hash things, or modify maps inside Commit/Blob objects.
    
    E.g. The `StagingArea` supports common map operations. Upon put (fileName, Commit), it completes: read commit into commit id -> put into its map -> serialize itself and write into the file for staging.

### Persistence

The directory structure looks like this:

```
CWD
 └──.gitlet
     └── --commits/       # all commits
        ├──blobs/         # file content
        ├──branchHeads/   # branch heads
        |  ├──--master      # master branch
        |  └──..            # other branches
        ├──HEAD	          # HEAD commit
        ├──add            # staging area for addition
        └──rm             # staging area for removal

```

The `Main` class is the entry class of the project. It is responsible for calling different functions according to given commands.

The `Repository` class will set up all persistance. It will

1. Create and initialize files and directories in the `.gitlet` folder if the directory does not exist;
2. Handle all updates of `HEAD` , `master`, `branchHeads` and the serialization of two StagingAreas `add` and `rm`.
3. Execute the commands / function calls from `Main`.

The `Commit` class handles the serialization of `Commit` objects. It also deals with conversion between commit ids and commit objects. Each `Commit` records mappings of held file names and their corresponding file content. Specifically, it fulfil the following purposes:

1. Constructs Commit objects;
2. Serializes and saves Commit objects to the .gitlet/commits directory;
3. Given a commit id, retrieves the corresponding Commit object.

The `Blob` class handles the serialization of `Blob` objects. A blob is a snapshot of a file's content at the moment of addition. For instance, a file named "hello.txt" can refer to different `Blobs` in different `Commits`. 

Its functions are similar to `Commit`, namely object construction, serialization and retrieval.

The `StagingArea` class stores files for addition and removal. A StagingArea works like a Java Map, stores mappings of file plain names to their blob ids, and supports basic map operations (`remove`, `get`, `put`). `add` and `rm` are `StagingAreas` for staged addition and removal respectively.

## Reference

Gitlet is originally an individual project in UCB [CS61b](https://sp21.datastructur.es/).
