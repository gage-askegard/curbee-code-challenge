# curbee-code-challenge
## Instructions
There is a basic main function with some preset data, running the function will print out diffs for the preset data, feel free to change data types and values and run as needed.
The diff on the complex type does not work sadly due to hashmap issues
## Development Process
Started around 11am. I started by creating a basic Kotlin project and made a first pass at what the general models and function signatures 
should be. Next, I took a stab at implementing the `DiffTool` class itself, where I originally went down a path of using reflection.
I'm not the most familiar with reflection as I usually try to avoid it due to performance costs, so I spent a good amount of time Googling
how to work with reflection in kotlin and trying to get type parameters correct. I realized (maybe a little too late) that
this was not the best path and I needed to find a cleaner solution. After being stumped for a while I decided to do what I would
do in the workplace (and would have done it sooner at work), ask a coworker for help and try to work through the problem with a different perspective.
I reached out a previous colleague of mine who I trust as an excellent engineer to see if we could generate some ideas. They
gave me the idea to serialize it to a string and then back to a map to more easily do the nested field level comparisons.
This was great and I went right back to writing the code independently after taking a break to pick up my kid and refresh my mind.
It was already 5:30 central, so I knew I was running on borrowed time at this point, but I wanted to complete the task still
and arrived at the current implementation. This also involved adding gradle for some dependency management, I originally went
the spring boot route to wire up a controller, but figured my time was better spent elsewhere. In the end I was not able to get the nested list portion, or the Audit key to work unfortunately.
The [de]serialization process wipes away the @AuditKey annotations, and it threw audit key exceptions when processing lists of complex objects.
Still I thank you for taking the time to look at this, and if you still want to consider me, I would be happy to talk further!