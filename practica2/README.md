## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

/*************** REGEX ***************/
GROUP 0 
^([0-9]{4}-[0-9]{2}-[0-9]{2})\s+([0-9]{2}:[0-9]{2}:[0-9]{2})\s+([^0-9]+)([0-9]+)\s+(\[\w+\]):(.*)

GROUP 1 == FECHA
[0-9]{4}-[0-9]{2}-[0-9]{2}

GROUP 2 == HORA
[0-9]{2}:[0-9]{2}:[0-9]{2}

GROUP 3 == TIPO_SERVIDOR
[^0-9]+

GROUP 4 == NUMERO_SERVIDOR
[0-9]+

GROUP 5 == ID_MENSAJE
\[\w+\]

GROUP 6 == MENSAJE
.*

/*************** JAVA ***************/
GROUP 0 
^([0-9]{4}-[0-9]{2}-[0-9]{2})\\s+([0-9]{2}:[0-9]{2}:[0-9]{2})\\s+([^0-9]+)([0-9]+)\\s+(\\[\\w+\\]):(.*)

GROUP 1 == FECHA
[0-9]{4}-[0-9]{2}-[0-9]{2}

GROUP 2 == HORA
[0-9]{2}:[0-9]{2}:[0-9]{2}

GROUP 3 == TIPO_SERVIDOR
[^0-9]+

GROUP 4 == NUMERO_SERVIDOR
[0-9]+

GROUP 5 == ID_MENSAJE
\\[\\w+\\]

GROUP 6 == MENSAJE
.*

/*************** MESSAGE REGEX & JAVA ***************/
^message from: (.+)to: (.+) message-id: (.+) size:.*

// Regex de inicio de palabra con digitos hasta el primer whitespace
^[^\s]+