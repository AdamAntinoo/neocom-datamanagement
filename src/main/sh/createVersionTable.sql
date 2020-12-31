-- NeoCom SDE database. Version 20190529
--
-- Table structure for table `version`
--

DROP TABLE IF EXISTS `version`;
CREATE TABLE `version` (
    `id` varchar(16),
    `versionNumber` integer NOT NULL
);
