# this is for macosx
#
# see: http://neo4j.com/docs/operations-manual/3.2/tutorial/import-tool/
# for using the "normal" ../neo/bin/neo4j-import tool

/Applications/Neo4j\ Community\ Edition\ 3.2.0.app/Contents/Resources/jre.bundle/Contents/Home/jre/bin/java \
-cp /Applications/Neo4j\ Community\ Edition\ 3.2.0.app/Contents/Resources/app/bin/neo4j-desktop-3.2.0.jar org.neo4j.tooling.ImportTool \
--into ../neo/data/graph.db \
--id-type string \
--nodes ./csv-files/granular-marking.csv \
--nodes ./csv-files/external-reference.csv \
--nodes ./csv-files/kill-chain-phase.csv \
--nodes ./csv-files/where_sighted_refs.csv \
--nodes ./csv-files/object_refs.csv \
--nodes ./csv-files/observed_data_refs.csv \
--nodes ./csv-files/attack-pattern.csv \
--nodes ./csv-files/campaign.csv \
--nodes ./csv-files/course-of-action.csv \
--nodes ./csv-files/identity.csv \
--nodes ./csv-files/indicator.csv \
--nodes ./csv-files/intrusion-set.csv \
--nodes ./csv-files/language-content.csv \
--nodes ./csv-files/malware.csv \
--nodes ./csv-files/marking-definition.csv \
--nodes ./csv-files/observed-data.csv \
--nodes ./csv-files/report.csv \
--nodes ./csv-files/threat-actor.csv \
--nodes ./csv-files/tool.csv \
--nodes ./csv-files/vulnerability.csv \
--relationships ./csv-files/granular-marking_rel.csv \
--relationships ./csv-files/where_sighted_refs_rel.csv \
--relationships ./csv-files/sighting_rel.csv \
--relationships ./csv-files/observed_data_refs_rel.csv \
--relationships ./csv-files/relationship_rel.csv \
--relationships ./csv-files/external-reference_rel.csv \
--relationships ./csv-files/kill-chain-phase_rel.csv \
--relationships ./csv-files/object_refs_rel.csv
