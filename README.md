# eBC EBooks Collection ToolBox

The eBooks Collection Toolbox (eBC) contains simple tools for
managing a collection of electronic documents written in Clojure.

It has the following features:

- The collection of documents is hierarchically organized in the
	filesystem by a simple naming convention.
- eBC generates a directory of HTML pages for browsing the
	collection.
- eBC generates (and updates) a full-text index of the ebooks
	collection (using the Apache Lucene library).
- eBC allows to search the full-text index.

The [manual](http://homepages.thm.de/~hg11260/mat/ebc.pdf) is available
from the projects [homepage](https://esb-dev.github.io/ebc.html).

## How to run eBC 
### Prerequisites 
- Install Java, Maven, Clojure and Leiningen. 
- Download the files or clone from git.

### Build 
- Run ``leiningen install`` within the root directory ``eBC/`` to download the needed dependencies
- (Run ``leiningen uberjar`` instead of ``leiningen jar`` to create executable ``ebc.jar``) 

### Start eBC 
- Without running ``leiningen uberjar`` you can run ``leiningen run`` to start the eBC from clojure sources
- After ``leiningen uberjar`` command you can execute the jar from the target folder with ``java -jar target/ebc.jar`` 

### Make MacOS Application
- The executable __makeapp__ within the ``ebc/`` directory creates installable .dmg bundle
- Run ``./makeapp`` to create the .dmg bundle in ``target/bundles/``
- Open the created ``./eBC-1.0.dmg`` and move __eBC.app__ to your Application directory 

## License

eBC is licensed under the Eclipse Public License (EPL). It uses libraries 
that are licensed under the Apache License 2.0 and the Affero General Public 
License (AGPL). 
