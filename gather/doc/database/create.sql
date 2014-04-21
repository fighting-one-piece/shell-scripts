CREATE DATABASE IF NOT EXISTS gather default charset utf8 COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `hot`;
CREATE TABLE `hot` (
  `autoid` 				int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `docno` 		    varchar(40) NOT NULL COMMENT '聚点文章',
  `channel` 			varchar(10) NOT NULL COMMENT '频道',
  `col` 			varchar(10) NOT NULL COMMENT '栏目',
  --`source` 				varchar(10) COMMENT '来源', --'163'说明由所有163文章聚类得来，目前是指专题
  `clustersize` int(5) NOT NULL COMMENT '聚类阈值',
  `starttime` 		datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '起始时间',
  `endtime` 			datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '截止时间',
  `createtime` 		datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  PRIMARY KEY  (`autoid`),
  KEY `hot_docno` (`docno`),
  KEY `hot_channel` (`channel`),
  KEY `hot_starttime` (`starttime`),
  KEY `hot_endtime` (`endtime`)
) ENGINE=INNODB ;


DROP TABLE IF EXISTS `doc`;
CREATE TABLE `doc` (
  `autoid` 				int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `docno` 		    varchar(40) NOT NULL COMMENT '文章编号',
  `title` 		    varchar(500) NOT NULL COMMENT '标题',
  `url` 		      varchar(1024) NOT NULL COMMENT '地址',
  `source` 				varchar(10) NOT NULL COMMENT '来源',
  `ptime` 				datetime NOT NULL COMMENT '发布时间',
  `channel` 			varchar(10) NOT NULL COMMENT '频道',
  `createtime` 		datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  PRIMARY KEY  (`autoid`),
  UNIQUE KEY `doc_docno` (`docno`),
  KEY `doc_channel` (`channel`),
  KEY `doc_source` (`source`),
  KEY `doc_ptime` (`ptime`),
  KEY `doc_createtime` (`createtime`)
) ENGINE=INNODB ;


DROP TABLE IF EXISTS `hot_doc`;
CREATE TABLE `hot_doc` (
  `autoid` 				int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `hotid` 		    int(10) NOT NULL COMMENT '聚点id',
  `docno` 		    varchar(40) NOT NULL COMMENT '文章编号',
  `createtime` 		datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  PRIMARY KEY  (`autoid`),
  KEY `hot_doc_hotid` (`hotid`),
  KEY `hot_doc_docid` (`docno`),
  KEY `hot_doc_createtime` (`createtime`)
) ENGINE=INNODB ;

DROP TABLE IF EXISTS `article_pushed`;
CREATE TABLE `article_pushed` (
  `autoid` 			int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `topicid` 		varchar(10) NOT NULL COMMENT '推送的栏目id',
  `title` 		    varchar(100) COMMENT '标题',
  `docno` 		    varchar(40) NOT NULL COMMENT '文章编号',
  `pushtime` 		timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '推送时间',
  `priority`		tinyint unsigned DEFAULT 60 COMMENT '文章推送的权重',
  PRIMARY KEY  (`autoid`),
  KEY `idx_tid_docno` (`topicid`,`docno`)
) ENGINE=INNODB ;


DROP TABLE IF EXISTS `special`;
CREATE TABLE `special` (
  `autoid` 				int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `docno` 		    varchar(40) COMMENT '文章编号',
  `col` 			varchar(10) NOT NULL COMMENT '栏目',
  `channel` 			varchar(10) NOT NULL COMMENT '频道',
  `del` 		    varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标志',
  `updatetime` 		datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `createtime` 		datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  PRIMARY KEY  (`autoid`),
  KEY `special_updatetime` (`updatetime`)
) ENGINE=INNODB ;

DROP TABLE IF EXISTS `special_doc`;
CREATE TABLE `special_doc` (
  `autoid` 				int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `specialid` 		int(10) NOT NULL COMMENT '专题id',
  `docno` 		    varchar(40) NOT NULL COMMENT '文章编号',
  `del` 		    varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标志',
  `createtime` 		datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  PRIMARY KEY  (`autoid`),
  KEY `special_doc_specialid` (`specialid`),
  KEY `special_doc_docno` (`docno`)
) ENGINE=INNODB ;

DROP TABLE IF EXISTS `picture`;
CREATE TABLE `picture` (
  `autoid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `setid` int(10) NOT NULL COMMENT '图集id',
  `photoid` varchar(20) NOT NULL COMMENT '图片id',
  `url` varchar(255) NOT NULL COMMENT '图片url',
  `description` varchar(1000) NOT NULL COMMENT '图片描述',
  PRIMARY KEY (`autoid`),
  KEY `picture_setid` (`setid`),
  KEY `picture_photoid` (`photoid`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `picture_set`;
CREATE TABLE `picture_set` (
  `autoid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `url` varchar(255) NOT NULL COMMENT '图集url',
  `title` varchar(255) NOT NULL COMMENT '图集title',
  `setid` int(10) NOT NULL COMMENT '图集id',
  `source` varchar(20) NOT NULL COMMENT '图集来源',
  `groupid` varchar(30) NOT NULL COMMENT '抓取分组',
  `jobid` varchar(30) NOT NULL COMMENT '抓取标识',
  `summary` varchar(1300) NOT NULL DEFAULT '' COMMENT '图集概述',
  `author` varchar(20) NOT NULL DEFAULT '' COMMENT '图集作者',
  `ptime` datetime DEFAULT NULL COMMENT '发布时间',
  `uploadnum` int(11) NOT NULL DEFAULT '0' COMMENT '上传图片数',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`autoid`),
  KEY `picture_set_setid` (`setid`),
  KEY `picture_set_source` (`source`),
  KEY `picture_set_title_author` (`title`,`author`)
) ENGINE=InnoDB 