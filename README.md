# Description

Mylyn does a great job building a context for our tasks. Yet, it is currently focused on files
and resources. However, nowadays the IDE has access to far more artifacts and activities that
can be mapped to a task.

This project aims on tracking other development related artifacts - like git repositories,
target platforms or breakpoints - and attach them to mylyn context. 

After we learned which git repositories are relevant to a task or which breakpoints are used,
we offer developers to import these breakpoints into their workspace or clone the relevant
git repositories automatically.

Therefore, provision their workspace without the hassle to create an maintain provisioning profiles.
In contrast to common workspace provisioning profiles this allows for both more specific and more
effortless management of profiles. As profiles are generated automatically, they're always up to date.
Also, we recognize task-related variations of the general development process. If you develop a single
task using a different branch in a scm, or using another target platform for compability reasons, 
every developer joining in to the task will automatically get notified.

![Mylyn Provisioning Screenshot](https://s3.amazonaws.com/sebs-misc/mylyn-prov.png)


# Installation

* (tested with the latest eclipse juno download)
* open eclipse, go to Help > Install New Software
* add mylyn nightly repository: http://download.eclipse.org/mylyn/snapshots/nightly/
* add mylyn provisioning update site: http://sschmidt.github.com/mylyn-provisioning/
* select Mylyn Provisioning Core Components and add the connectors you like
* hit next and follow the wizard 

# Connectors

Similar to Mylyn context, this project provides a basic framework to execute provisioning
tasks. The specific tasks however are implemented by connectors, which are attached with
the help of extension points. We currently supply connectors for:

* Git repositories
* Target platforms
* Breakpoints 

Visit the wiki to learn more about how to create your own connector:
https://github.com/sschmidt/edu.tum.cs.mylyn.provisioning/wiki/Connectors


# Further information

We're currently conducting a research questionnaire to learn more about the requirements
of workspace provisioning tools. Visit our survey page to participate:
https://www1.cs.tum.edu/survey/index.php/survey/index/sid/832249
There's also a nice giveaway ;)

This project is based on a former 2012 Google Summer of Code project. Learn more:
http://wiki.eclipse.org/Mylyn/Enriching_Task_Context_with_Breakpoints 

# License

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

# Contact 

* Sebastian Schmidt <schmidse@cs.tum.edu>
* Nitesh Narayan <narayan@cs.tum.edu>
