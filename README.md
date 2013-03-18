# Description

Mylyn does a great job building a context for our tasks. Yet, it is currently focused on files
and resources. However, nowadays the IDE has access to far more artifacts and activities that
can be mapped to a task.

This project aims on tracking other development related artifacts - like git repositories,
target platforms or breakpoints - and attach them to mylyn context. Tracking those artifacts
allow us to provision a developer workspace based on a mylyn task. We can use mylyn's 
functionality to copy and share a context with other developers in order to help them use
the appropriate target platform or clone the right bits from a git repository. 

![Mylyn Provisioning Screenshot](https://s3.amazonaws.com/sebs-misc/mylyn-prov.png)

# Installation

* (tested with the latest eclipse juno download)
* open eclipse, go to Help > Install New Software
* add mylyn nightly repository: http://download.eclipse.org/mylyn/snapshots/nightly/
* add mylyn provisioning update site: http://sschmidt.github.com/mylyn-provisioning/
* select Mylyn Provisioning Core Components and add the connectors you like
* hit next and follow the wizard 

# Further information

We're currently conducting a research questionnaire to learn more about the requirements
of workspace provisioning tools. Head over to http://www1.cs.tum.edu/survey to participate.

# License

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

# Contact 

* Sebastian Schmidt <schmidse@cs.tum.edu>
* Nitesh Narayan <narayan@cs.tum.edu>
