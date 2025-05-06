import jenkins.model.*
import java.util.logging.Logger

def plugins = [
    "maven-plugin",               // Maven Integration Plugin
    "pipeline-maven",             // Pipeline Maven Integration Plugin
    "docker-plugin",              // Docker Plugin
    "docker-workflow",            // Docker Pipeline Plugin
    "docker-build-publish",       // CloudBees Docker Build and Publish Plugin
    "credentials",                // Credentials Plugin
    "credentials-binding",        // Credentials Binding Plugin
    "git",                        // Git Plugin
    "workflow-aggregator",        // Pipeline Plugin
    "ws-cleanup",                 // Workspace Cleanup Plugin
    "config-file-provider"        // Config File Provider Plugin
]
def instance = Jenkins.getInstance()
def pm = instance.getPluginManager()
def uc = instance.getUpdateCenter()

plugins.each { pluginName ->
    if (!pm.getPlugin(pluginName)) {
        def plugin = uc.getPlugin(pluginName)
        if (plugin) {
            plugin.deploy()
        }
    }
}
instance.save()