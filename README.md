# Application Setup
Clone this repository to your local machine:

git clone https://github.com/chalameg/crowdfunding-backend.git
Open a terminal and navigate to the project directory.

# Prerequisites
Java 17 or later
Maven
PostgreSQL server installed and running
PostgreSQL client or the command line tool

# Setting up the database
This Spring Boot application uses a PostgreSQL database called "crowdfunding".
Before running the application, you must create the database and set the password to "1313".
Or you can create a database with a name and password of your own and replace the old one in \src\main\resources\application.properties.
To create the database and set the password, you can use the following SQL commands:

CREATE DATABASE crowdfunding;
ALTER USER postgres PASSWORD '1313';

Please note that the above commands assume that you are using the default "postgres" user for your PostgreSQL installation.
If you are using a different user, you will need to modify the commands accordingly.

# Executing the query
Before using the application, you must execute the following query in your PostgreSQL database:

ALTER TABLE campaign ADD COLUMN document tsvector;
UPDATE campaign set document = setweight(array_to_tsvector((select array_agg(distinct substring(lexeme for len))
from unnest(to_tsvector(title)), generate_series(1,length(lexeme)) len)),'A') ||
setweight(to_tsvector(coalesce(short_description, '')), 'B') ||
setweight(to_tsvector(coalesce(city, '')), 'C');
CREATE INDEX document ON campaign USING GIN (document);

CREATE FUNCTION campaign_tsvector_trigger() RETURNS trigger As $$
begin
new.document =setweight(array_to_tsvector((select array_agg(distinct substring(lexeme for len))
from unnest(to_tsvector(new.title)), generate_series(1,length(lexeme)) len)),'A') ||
setweight(to_tsvector(coalesce(new.short_description, '')), 'B') ||
setweight(to_tsvector(coalesce(new.city, '')), 'C');
return new;
end
$$ LANGUAGE plpgsql;
CREATE TRIGGER tsvectorupdate BEFORE INSERT OR UPDATE ON campaign FOR EACH ROW EXECUTE PROCEDURE campaign_tsvector_trigger();

# Troubleshooting
If you encounter any issues while setting up or running the application,
please check the application logs for more information. You can also open an issue on the project's GitHub page.

# Additional information
For more information on how to use the Spring Boot framework, please consult the official documentation at https://spring.io/projects/spring-boot. 
For more information on how to use PostgreSQL, please consult the official documentation at https://www.postgresql.org/docs/.

# Using the application
Once you have set up the database and executed the query, you can use the application to create, read, update, and delete crowdfunding projects.
The application will be available at http://localhost:8080.
The application provides REST endpoints for these operations. You can get detailed information about endpoints in the controller part of this project.
