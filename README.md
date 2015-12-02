jClipCorn
------------------
*a portable movie and series manager for Windows and Linux*



> **Disclaimer:**  
> Use this software only in a country which allows for private copies of your bought DVD's.  
> Use this software only to manage copies of movies which you own.  

![main-view](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/main-view.png)

### Main Features

 - Fully portable - leave the program, the database and the settings on the same *(external)* drive as your movies
 - Multi language (German & English), if you want you can help me translate it into other languages.
 - Parse meta data from the [IMDB](http://www.imdb.com/)
 - Filter your movies by many different criterias (date, genre, name, film series, quality, language, MPAA rating ...)
 - Remember which movies you have watched and where you are in a series
 - Automatically generate nice file names and folder structures for your movies and series
 - Generate statistics from your ClipCorn database
 - Tag and rate your watched movies
 - Works under Windows, Linux *(and probably OS X)*

###[> DOWNLOAD](github.com/Mikescher/jClipCorn/releases)
###[> HOMEPAGE](www.mikescher.de/programs/view/jClipCorn)

### Organization

The idea of ClipCorn is to have all movies in a single folder together with the ClipCorn executable and its database on an external hard drive.  
You can use it in a different way, but for the best user experience it's recommended to follow the organization guide for which this tool was developed.

Every movie is named by a specific pattern. After you have added movies to your database you can rename the files to this pattern with the *"Check Database"* menu entry.
The movies are afterwards named like this:

~~~
Stirb Langsam IV (Part 2).avi
Spongebob - Lost in Time.mpg
Terminator II - Tag der Abrechnung (Part 1).avi
X-Men I.avi
X-Men Origins - Wolverine.avi
X-Men III.avi
Shadowless Sword (Part1).avi
Forrest Gump [Eng].mpg
Terminator II - Tag der Abrechnung [Ger] (Part 1).avi
~~~
*(the exact rules, encoded as an [EBNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_Form) can be found under the help menu)*

Series have an unique folder per series (with the series name), after that a folder for each season and then files with the file name pattern `SxxExx - %name%`. For example: `S01E07 - You Win or You Die.avi`  
You can create the folder structure for series by right clicking on a series and selecting *"Create folder structure"*

### First start

First copy the executable (either jClipCorn.jar or jClipCorn.exe) into the folder where you have your movies, then execute it (it needs read and write permissions).

On the first start it should create a folder "*ClipCornDB*" where the database is stored.

![main-empty-metal](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/main-empty-metal.png)

You can see this in the log view (icon in the bottom status bar, to the right of the progress bar).

![log-recreate](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/log-recreate.png)

At first you should look into the settings and eventually change a few things, important are:

 - UI Language
 - Look'n'Feel (It's recommended to either use *Windows* or *Metal*)
 - VLC Path (path to *vlc.exe*, for playing. You can also use another player - but it works best with the [VLC player](http://www.videolan.org/vlc/))
 - The backup settings (I recommend to **enable** automatic backups *(like every 7 days or so)*)
 - Automatic relative paths (this should really stay active - otherwise paths will be absolute and everything can go havoc if ClipCorn runs on an external drive)

After that you can start adding your movies and series.

### Adding a movie

Either go through the menu (*Movies* -> *Add Movie*) or through the toolbar icon or throgh the default shortcut *[Ctrl]*+*[I]*.

> **Tip:**  
> Under the menu *Extras* -> *Scan folder* you can find all movies/series in a folder which are not part of the current database.

![addmovie-empty](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addmovie-empty.png)

Now you choose the movie file. If the file has already the correct (or a nearly correct) naming pattern, information will be added automatically in the respective fields.  
For example here the second part is automatically inserted.
Also the name, film series, format, size, language and quality is filled in.

![addmovie-insert](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addmovie-insert.png)

For the rest of meta data we click on **parse Movie online** and the data is queries from [IMDB.com](imdb.com)

> **Tip:**  
> If you can't find the movie on IMDB (but are sure it's online), try the *extended parse* button

![addmovie-parse](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addmovie-parse.png)

After that all information is available and with **OK** you can insert the movie. But you can also manually change ome fields or search online for a different cover (instead of the IMDB default cover).

![addmovie-final](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addmovie-final.png)

> **Tip:**  
> With the **Crop** Button you can edit a cover which has the wrong proportions (or includes the backside)

### User Interface

You can reach much all important functions through the main window:

![main-view](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/main-view.png)

In the middle are all the movies. Click once to select the movie and see it's cover.  
Double-Click to play a movie (multi-part movies are automatically added to the VLC playlist).  
You can search for a movie with the top-right searchbar.  
You can filter movies either by meta data (list on the left side) or by the initial character (bottom bar).

Beside the movies you can see additional information. (If you have watched the movie *(= the eye)*, the IMDB score, your own rating, etc)

If you want ore information about a movie you can select *"Preview"* from the context menu

![movie-preview](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/movie-preview.png)

By double-clicking a series you will get an overview over all seasons and which episodes you have already watched.

![series-preview](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/series-preview.png)

With the button "*Play next episode*" ClipCorn will try to automatically find the next episode you need to watch and play it.

### Adding a series

Adding a series is a little bit more complex than adding a movie.

Open the "Add series" dialog via the menu, or the toolbar.  
Similar to the adding of a movie, insert the series-name and query the rest of the information from the IMDB
.
![addseries-initial](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addseries-initial.png)

Now the "edit series" window opens. Initially without any seasons or episodes.

![addseries-edit](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addseries-edit.png)

Here we firstly add a new season ("*add series*" button)

![addseries-addseason](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addseries-addseason.png)

The "add season" dialog is pretty self-explanatory.

> **Important:**  
> Seasons are sorted by an lexicographical order. That means *Season 1* will always be before *Season 2*, independent on the insert order.  
> So if your series has more than ten seasons you should name the first season *Season 01*, not *Season 1*

![addseries-edit2](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addseries-edit2.png)

After we have added a season we can see it in our "edit series" window.
Now we add episodes to this series. We can either do this manual for each episode (with the "*Add empty episode*" button) or in bulk with the "*add multiple episodes*" button (which I will explain here).

In the upcoming dialog you should first add all episodes with the **[Add]** button.  

![addseries-addepisodes-initial](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addseries-addepisodes-initial.png)

Then click on the *Autom. calc metadata* button to update the meta data of all episodes.

You can now either

 - insert the episode title manually on each episode (*Click ENTER to switch to the next entry after changing the episode title*)
 - Extract it from the file name with the left string-manipulation controls (*only works for simple file names which contain the episode title*)
 - Use the Omniparser to parse the titles from folder names/file names/other sources

![addseries-addepisodes-omniparser](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/addseries-addepisodes-omniparser.png)

The omniparser tries automatically to extract the raw episode titles in three steps, most of the time no (or almost no) manual after-editing is needed.

>**Tip:**  
>A really good source for episode titles are the Wikipedia episode lists.  
>On Firefox hold *[Ctrl]* and click on all the table cells with the episode titles. After that  you can copy them with *[Ctrl]*+*[C]* and insert them into the omniparser.  
>Because this way you will get one episode per line you can directly insert them into the step-2 text field.  

### Check Database for errors

Under the menu entry *Database* -> *Validate Database* you can test your database for problems.

Common problems are:

####Wrong file names for movies

This can either be manually corrected or by clicking "*Try to auto. fix problems*".

####Wrong folder structure for series

Right-click on a series and click on "*Generate folder structure*".  
Here you need to choose the folder where **all** the series are located *(not the folder of the individual series)*.  
After that you see the changes that will be made on the file system by clicking on *test*. 

![generate-folder-structure](https://raw.github.com/Mikescher/jClipCorn/master/README-FILES/generate-folder-structure.png)

 - **red** are errors
 - **yellow** are no-changes
 - **green** are to-be-moved files.
  
By clicking on *[OK]* you execute the changes.

>**Tip:**  
>It's always recommended to first click on [Test] to preview the changes.