create database HR;
use HR;
CREATE TABLE Region (
    Region_ID INT PRIMARY KEY,
    Region_Name VARCHAR(255)
);
INSERT INTO Region (Region_ID, Region_Name) VALUES
(1, 'Europe'),
(2, 'Americas'),
(3, 'Asia'),
(4, 'Middle East and Africa');

CREATE TABLE Countries (
    Country_ID VARCHAR(3) PRIMARY KEY,
    Country_Name VARCHAR(255),
    Region_ID INT,
    FOREIGN KEY (Region_ID) REFERENCES Region(Region_ID)
);

INSERT INTO Countries (Country_ID, Country_Name, Region_ID) VALUES
('AR', 'Argentina', 2),
('AU', 'Australia', 3),
('BE', 'Belgium', 1),
('US', 'USA', 2),
('CA', 'Canada', 2),
('CH', 'Switzerland', 1),
('CN', 'China', 3),
('DE', 'Germany', 1),
('DK', 'Denmark', 1),
('EG', 'Egypt', 4),
('FR', 'France', 1),
('IL', 'Israel', 4);
INSERT INTO Countries (Country_ID, Country_Name, Region_ID) VALUES ('IT', 'Italy', 1);
INSERT INTO Countries (Country_ID, Country_Name, Region_ID) 
VALUES 
('IN', 'India', 3),    -- India in Region 3 (Asia)
('SG', 'Singapore', 3),  -- Singapore in Region 3 (Asia)
('JP', 'Japan', 3),    -- Japan in Region 3 (Asia)
('NL', 'Netherlands', 1),  -- Netherlands in Region 1 (Europe)
('MX', 'Mexico', 2),   -- Mexico in Region 2 (Americas)
('BR', 'Brazil', 2);   -- Brazil in Region 2 (Americas)
INSERT INTO Countries (Country_ID, Country_Name, Region_ID) VALUES ('UK', 'United Kingdom', 1); -- 'UK' belongs to Region 1 (Europe)


CREATE TABLE Locations (
    Location_id INT PRIMARY KEY,
    Street_Address VARCHAR(255),
    Postal_Code VARCHAR(10),
    City VARCHAR(255),
    State_Province VARCHAR(255),
    Country_ID VARCHAR(3),
    FOREIGN KEY (Country_ID) REFERENCES Countries(Country_ID)
);

-- Insert data into the Locations table with valid Country_ID values
INSERT INTO Locations (Location_id, Street_Address, Postal_Code, City, State_Province, Country_ID) VALUES
(1000, '1297 Via Cola di Rie', '989', 'Roma', NULL, 'IT'),
(1100, '93091 Calle della Testa', '10934', 'Venice', NULL, 'IT'),
(1200, '2017 Shinjuku-ku', '1689', 'Tokyo', 'Tokyo Prefecture', 'JP'),
(1300, '9450 Kamiya-cho', '6823', 'Hiroshima', NULL, 'JP'),
(1400, '2014 Jabberwocky Rd', '26192', 'Southlake', 'Texas', 'US'),
(1500, '2011 Interiors Blvd', '99236', 'South San Francisco', 'California', 'US'),
(1600, '2007 Zagora St', '50090', 'South Brunswick', 'New Jersey', 'US'),
(1700, '2004 Charade Rd', '98199', 'Seattle', 'Washington', 'US'),
(1800, '147 Spadina Ave', 'M5V 2L7', 'Toronto', 'Ontario', 'CA'),
(1900, '6092 Boxwood St', 'YSW 9T2', 'Whitehorse', 'Yukon', 'CA'),
(2000, '40-5-12 Laogianggen', '190518', 'Beijing', NULL, 'CN'),
(2100, '1298 Vileparle (E)', '490231', 'Bombay', 'Maharashtra', 'IN'),
(2200, '12-98 Victoria Street', '2901', 'Sydney', 'New South Wales', 'AU'),
(2300, '198 Clementi North', '540198', 'Singapore', NULL, 'SG'),
(2400, '8204 Arthur St', NULL, 'London', NULL, 'UK'),
(2500, 'Magdalen Centre, The Oxford Science Park', 'OX9 9ZB', 'Oxford', 'Oxford', 'UK'),
(2600, '9702 Chester Road', '9629850293', 'Stretford', 'Manchester', 'UK'),
(2700, 'Schwanthalerstr. 7031', '80925', 'Munich', 'Bavaria', 'DE'),
(2800, 'Rua Frei Caneca 1360', '01307-002', 'Sao Paulo', 'Sao Paulo', 'BR'),
(2900, '20 Rue des Corps-Saints', '1730', 'Geneva', 'Geneve', 'CH'),
(3000, 'Murtenstrasse 921', '3095', 'Bern', 'BE', 'CH'),
(3100, 'Pieter Breughelstraat 837', '3029SK', 'Utrecht', 'Utrecht', 'NL'),
(3200, 'Mariano Escobedo 9991', '11932', 'Mexico City', 'Distrito Federal', 'MX');


CREATE TABLE Departments (
    DEPARTMENT_ID INT PRIMARY KEY,
    DEPARTMENT_NAME VARCHAR(255),
    MANAGER_ID INT,
    LOCATION_ID INT,
    FOREIGN KEY (LOCATION_ID) REFERENCES Locations(Location_id)
);

INSERT INTO Departments (DEPARTMENT_ID, DEPARTMENT_NAME, MANAGER_ID, LOCATION_ID) VALUES
(10, 'Administration', 200, 1700),
(20, 'Marketing', 201, 1800),
(30, 'Purchasing', 114, 1700),
(40, 'Human Resources', 203, 2400),
(50, 'Shipping', 121, 1500),
(60, 'IT', 103, 1400),
(70, 'Public Relations', 204, 2700),
(80, 'Sales', 145, 2500),
(90, 'Executive', 100, 1700),
(100, 'Finance', 108, 1700),
(110, 'Accounting', 205, 1700),
(120, 'Treasury', 0, 1700),
(130, 'Corporate Tax', 0, 1700),
(140, 'Control And Credit', 0, 1700),
(150, 'Shareholder Services', 0, 1700),
(160, 'Benefits', 0, 1700),
(170, 'Manufacturing', 0, 1700),
(180, 'Construction', 0, 1700),
(190, 'Contracting', 0, 1700),
(200, 'Operations', 0, 1700),
(210, 'IT Support', 0, 1700),
(220, 'NOC', 0, 1700),
(230, 'IT Helpdesk', 0, 1700),
(240, 'Government Sales', 0, 1700),
(250, 'Retail Sales', 0, 1700),
(260, 'Recruiting', 0, 1700);

CREATE TABLE Jobs (
    Job_ID VARCHAR(10) PRIMARY KEY,
    Job_Title VARCHAR(255),
    Min_Salary INT,
    Max_Salary INT
);

INSERT INTO Jobs (Job_ID, Job_Title, Min_Salary, Max_Salary) VALUES
('AD_PRES', 'President', 20080, 40000),
('AD_VP', 'Administration Vice President', 15000, 30000),
('FI_ACCOUNT', 'Accountant', 4200, 9000),
('FI_MGR', 'Finance Manager', 8200, 16000),
('IT_PROG', 'Programmer', 4000, 10000);

CREATE TABLE Employee (
    EMPLOYEE_ID INT PRIMARY KEY,
    FIRST_NAME VARCHAR(50),
    LAST_NAME VARCHAR(50),
    EMAIL VARCHAR(100),
    PHONE_NUMBER VARCHAR(20),
    HIRE_DATE DATE,
    JOB_ID VARCHAR(10),
    SALARY DECIMAL(10, 2),
    COMMISSION_PCT DECIMAL(5, 2),
    MANAGER_ID INT,
    DEPARTMENT_ID INT,
    FOREIGN KEY (JOB_ID) REFERENCES Jobs(Job_ID),
    FOREIGN KEY (DEPARTMENT_ID) REFERENCES Departments(DEPARTMENT_ID)
);

-- Insert data for the first three employees
INSERT INTO Employee (EMPLOYEE_ID, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, HIRE_DATE, JOB_ID, SALARY, COMMISSION_PCT, MANAGER_ID, DEPARTMENT_ID) VALUES
(100, 'Steven', 'King', 'SKING', '515.123.4567', '2003-06-17', 'AD_PRES', 24000.00, 0.00, 0, 90),
(101, 'Neena', 'Kochhar', 'NKOCHHAR', '515.123.4568', '2005-09-21', 'AD_VP', 17000.00, 0.00, 100, 90),
(102, 'Lex', 'De Haan', 'LDEHAAN', '515.123.4569', '2001-01-13', 'AD_VP', 17000.00, 0.00, 100, 90),
(103, 'Alexander', 'Hunold', 'AHUNOLD', '515.123.4567', '2006-01-03', 'IT_PROG', 9000.00, 0.00, 102, 60),
(104, 'Bruce', 'Ernst', 'BERNST', '590.423.4568', '2007-05-21', 'IT_PROG', 6000.00, 0.00, 103, 60),
(105, 'David', 'Austin', 'DAUSTIN', '590.423.4569', '2005-06-25', 'IT_PROG', 4800.00, 0.00, 103, 60),
(106, 'Valli', 'Pataballa', 'VPATABAL', '590.423.4560', '2007-02-07', 'IT_PROG', 4800.00, 0.00, 103, 60),
(107, 'Diana', 'Lorentz', 'DLORENTZ', '590.423.5567', '2007-02-07', 'IT_PROG', 4200.00, 0.00, 103, 60),
(108, 'Nancy', 'Greenberg', 'NGREENBE', '515.124.4569', '2002-08-17', 'FI_MGR', 12008.00, 0.00, 101, 100),
(109, 'Daniel', 'Faviet', 'DFAVIET', '515.124.4169', '2002-08-16', 'FI_ACCOUNT', 9000.00, 0.00, 108, 100),
(110, 'John', 'Chen', 'JCHEN', '515.124.4269', '2005-09-28', 'FI_ACCOUNT', 8200.00, 0.00, 108, 100),
(111, 'Ismael', 'Sciarra', 'ISCIARRA', '515.124.4369', '1005-09-30', 'FI_ACCOUNT', 7700.00, 0.00, 108, 100);



# ASSIGNMENT 2 Part 1
# Q1 (a)
SELECT R.Region_Name, C.Country_Name, COUNT(L.Location_id) AS Number_of_Locations
FROM Region AS R
LEFT JOIN Countries AS C ON R.Region_ID = C.Region_ID
LEFT JOIN Locations AS L ON C.Country_ID = L.Country_ID
GROUP BY R.Region_Name, C.Country_Name
ORDER BY Number_of_Locations DESC;

# Q1 (b)
select E.LAST_NAME, E.FIRST_NAME, E.EMPLOYEE_ID, D.DEPARTMENT_NAME, J.JOB_TITLE, E.SALARY
from Employee E
join Departments D ON E.DEPARTMENT_ID = D.DEPARTMENT_ID
join Jobs J ON E.JOB_ID = J.JOB_ID
where E.SALARY between 1000 AND 2000
order by E.LAST_NAME, E.FIRST_NAME;

# Assignment 2 Part 2
# Q2 (a)
SELECT D.DEPARTMENT_NAME, 
       COUNT(E.EMPLOYEE_ID) AS Number_of_Employees,
       MIN(E.SALARY) AS Min_Salary,
       MAX(E.SALARY) AS Max_Salary,
       AVG(E.SALARY) AS Average_Salary
FROM Departments D
LEFT JOIN Employee E ON D.DEPARTMENT_ID = E.DEPARTMENT_ID
GROUP BY D.DEPARTMENT_NAME
UNION ALL
# Query to retrieve totals for the entire company
SELECT 'Totals' AS DEPARTMENT_NAME,
       COUNT(E.EMPLOYEE_ID) AS Number_of_Employees,
       MIN(E.SALARY) AS Min_Salary,
       MAX(E.SALARY) AS Max_Salary,
       AVG(E.SALARY) AS Average_Salary
FROM Employee E;

# Q2 (b)
SELECT E1.EMPLOYEE_ID AS EMPLOYEE_ID, E1.LAST_NAME AS EmployeeLastName, E1.FIRST_NAME AS EmployeeFirstName,
    E2.EMPLOYEE_ID AS FirstLevelManagerID,E2.LAST_NAME AS FirstLevelManagerLastName, E2.FIRST_NAME AS FirstLevelManagerFirstName,
    E3.EMPLOYEE_ID AS SecondLevelManagerID, E3.LAST_NAME AS SecondLevelManagerLastName, E3.FIRST_NAME AS SecondLevelManagerFirstName,
    E4.EMPLOYEE_ID AS ThirdLevelManagerID,E4.LAST_NAME AS ThirdLevelManagerLastName, E4.FIRST_NAME AS ThirdLevelManagerFirstName
FROM Employee E1
LEFT JOIN Employee E2 ON E1.MANAGER_ID = E2.EMPLOYEE_ID
LEFT JOIN Employee E3 ON E2.MANAGER_ID = E3.EMPLOYEE_ID
LEFT JOIN Employee E4 ON E3.MANAGER_ID = E4.EMPLOYEE_ID;








