#!/bin/sh

gethtmlsh=/home/workspace/tagcloud/current-release/src/main/shell/getHTML.sh
basehtml_include=/home/workspace/gather/html/
cnav2013_css=http://news.163.com/special/f2e/cnav2013_css.html
ntes_js=http://news.163.com/special/ntes_common_model/ntes_js.html

mkdir -p $basehtml_include

/bin/sh $gethtmlsh "${nav_top_URL}" 200 ${basehtml_include}/nav_top_bak.html ${basehtml_include}/nav_top.html false true
/bin/sh $gethtmlsh "${nav_end_URL}" 200 ${basehtml_include}/nav_end_bak.html ${basehtml_include}/nav_end.html false true
/bin/sh $gethtmlsh "${ntes_common_head_v2_URL}" 20 ${basehtml_include}/ntes_common_head_v2_bak.html ${basehtml_include}/ntes_common_head_v2.html false true
/bin/sh $gethtmlsh "${ntes_js_URL}" 20 ${basehtml_include}/ntes_js_bak.html ${basehtml_include}/ntes_js.html false true
/bin/sh $gethtmlsh "${ntes_nav_index_v2_URL}" 200 ${basehtml_include}/ntes_nav_index_v2_bak.html ${basehtml_include}/ntes_nav_index_v2.html false true
/bin/sh $gethtmlsh "${ntes_nav_bottom_v2_URL}" 200 ${basehtml_include}/ntes_nav_bottom_v2_bak.html ${basehtml_include}/ntes_nav_bottom_v2.html false true
/bin/sh $gethtmlsh "${Nielsen_0001_URL}" 200 ${basehtml_include}/Nielsen0001_bak.html ${basehtml_include}/Nielsen0001.html false true
/bin/sh $gethtmlsh "${Nielsen_0005_URL}" 200 ${basehtml_include}/Nielsen0005_bak.html ${basehtml_include}/Nielsen0005.html false true
/bin/sh $gethtmlsh "${Nielsen_0008_URL}" 200 ${basehtml_include}/Nielsen0008_bak.html ${basehtml_include}/Nielsen0008.html false true
/bin/sh $gethtmlsh "${Nielsen_0009_URL}" 200 ${basehtml_include}/Nielsen0009_bak.html ${basehtml_include}/Nielsen0009.html false true
/bin/sh $gethtmlsh "${Nielsen_0025_URL}" 200 ${basehtml_include}/Nielsen0025_bak.html ${basehtml_include}/Nielsen0025.html false true
/bin/sh $gethtmlsh "${auto_rank}" 200 ${basehtml_include}/auto_rank_bak.html ${basehtml_include}/auto_rank.html false true
