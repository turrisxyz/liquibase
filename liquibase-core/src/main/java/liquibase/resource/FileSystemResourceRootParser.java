package liquibase.resource;

import java.io.File;

/**
 * {@link ResourceRootParser} that converts the path into a {@link FileSystemResourceAccessor}.
 */
public class FileSystemResourceRootParser extends AbstractResourceRootParser {

    /**
     * Returns {@link #PRIORITY_DEFAULT} for all paths except for ones that are for a non-"file:" protocol.
     */
    public int getPriority(String root) {
        if (root == null) {
            return PRIORITY_NOT_APPLICABLE;
        }

        if (!root.startsWith("/") && root.contains(":")) {
            if (root.startsWith("file:") || root.matches("^[A-Za-z]:.*")) {
                return PRIORITY_DEFAULT;
            } else {
                return PRIORITY_NOT_APPLICABLE;
            }
        }
        return PRIORITY_DEFAULT;
    }

    public ResourceAccessor parse(String root) {
        return new FileSystemResourceAccessor(new File(root));
    }
}
