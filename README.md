# Architecture
<p align="center">
  <img src="docs/images/architecture.png">
</p>

# Database diagram
<p align="center">
  <img src="docs/images/database.png">
</p>

# Documentation 
Via swagger : [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html#/)

# Database credentials
| Property | Value                              |
|----------|------------------------------------|
| Hostname | `manny.db.elephantsql.com`         |
| Name     | `eymotfhi`                         |
| Port     | `5432`                             |
| Username | `eymotfhi`                         |
| Password | `dqJhIIFxZs3elscoVytE1t2LOOT-qZm3` |

# Course material
| Session | Date       | Instructor                                        | Description                                                                                                                              |
|---------|------------|---------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| `#01`   | 02/04/2024 | [elieahd](https://github.com/elieahd)             | [Overview of Web development](https://drive.google.com/file/d/11One7fJDrS5ji3vojavXtsX70_DcqRoM/view?usp=drive_link)                     |
| `#02`   | 03/04/2024 | [bilal-elchami](https://github.com/bilal-elchami) | [Angular Getting Started](https://drive.google.com/file/d/1FzGDdODKGF6JPQkFPthgcJGCneJQdTVv/view?usp=drive_link)                         |
| `#03`   | 22/04/2024 | [elieahd](https://github.com/elieahd)             | [Building a backend application with Spring Boot](https://drive.google.com/file/d/1t2Gca1C1giOdv3LYJIkvH_a4GfcPC38L/view?usp=drive_link) |
| `#04`   | 25/04/2024 | [elieahd](https://github.com/elieahd)             | [Implementing RESTful API with JPA](https://drive.google.com/file/d/1EKiskNB5uvD7SV2sKR-QN_Ae-QTBiJ1A/view?usp=drive_link)               |
| `#05`   | 13/05/2024 | [bilal-elchami](https://github.com/bilal-elchami) | `tbd`                                                                                                                                    |
| `#06`   | 16/05/2024 | [bilal-elchami](https://github.com/bilal-elchami) | `tbd`                                                                                                                                    |
| `#07`   | 03/06/2024 | [elieahd](https://github.com/elieahd)             | `tbd`                                                                                                                                    |
| `#08`   | 06/06/2024 | [bilal-elchami](https://github.com/bilal-elchami) | `tbd`                                                                                                                                    |
| `#09`   | 17/06/2024 | [elieahd](https://github.com/elieahd)             | `tbd`                                                                                                                                    |
| `#10`   | 20/06/2024 | [bilal-elchami](https://github.com/bilal-elchami) | `tbd`                                                                                                                                    |

# Pipelines
| Event     | Description                                     | Workflow                                                   |
|-----------|-------------------------------------------------|------------------------------------------------------------|
| on `push` | checkout code, build project and run unit tests | [`.github/workflows/test.yml`](.github/workflows/build-test.yml) |