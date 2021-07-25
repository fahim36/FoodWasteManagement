-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 23, 2018 at 04:54 PM
-- Server version: 5.7.22-0ubuntu0.16.04.1
-- PHP Version: 7.0.30-0ubuntu0.16.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `HungerCure`
--

-- --------------------------------------------------------

--
-- Table structure for table `admininfotable`
--

CREATE TABLE `admininfotable` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `admininfotable`
--

INSERT INTO `admininfotable` (`username`, `password`) VALUES
('k', 'k');

-- --------------------------------------------------------

--
-- Table structure for table `userinformationtable`
--

CREATE TABLE `userinformationtable` (
  `username` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phoneno` varchar(10) NOT NULL,
  `password` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `userinformationtable`
--

INSERT INTO `userinformationtable` (`username`, `email`, `phoneno`, `password`) VALUES
('k', 'k', '1', 'k');

-- --------------------------------------------------------

--
-- Table structure for table `userrequesttable`
--

CREATE TABLE `userrequesttable` (
  `phoneno` varchar(255) NOT NULL,
  `locationtext` varchar(255) DEFAULT NULL,
  `latitude` varchar(255) DEFAULT NULL,
  `longitude` varchar(255) DEFAULT NULL,
  `fooddescription` varchar(255) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `userinformationtable`
--
ALTER TABLE `userinformationtable`
  ADD PRIMARY KEY (`phoneno`),
  ADD UNIQUE KEY `phoneno` (`phoneno`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
