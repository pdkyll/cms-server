INSERT INTO cms_object
	(name, name_path, "view", published)
SELECT DISTINCT
	'root', 'root', 'root', true
FROM cms_object
WHERE name = 'root' and parent_id is null
HAVING count(*) = 0;
