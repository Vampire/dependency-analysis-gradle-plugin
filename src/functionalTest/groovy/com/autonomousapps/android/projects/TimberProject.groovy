package com.autonomousapps.android.projects

import com.autonomousapps.AbstractProject
import com.autonomousapps.advice.Advice
import com.autonomousapps.advice.ComprehensiveAdvice
import com.autonomousapps.advice.Dependency
import com.autonomousapps.advice.PluginAdvice
import com.autonomousapps.kit.*

import static com.autonomousapps.kit.Dependency.appcompat
import static com.autonomousapps.kit.Dependency.timber

final class TimberProject extends AbstractProject {

  final GradleProject gradleProject
  private final String agpVersion

  TimberProject(String agpVersion) {
    this.agpVersion = agpVersion
    this.gradleProject = build()
  }

  private GradleProject build() {
    def builder = newGradleProjectBuilder()
    builder.withRootProject { r ->
      r.gradleProperties = GradleProperties.minimalAndroidProperties()
      r.withBuildScript { bs ->
        bs.buildscript = BuildscriptBlock.defaultAndroidBuildscriptBlock(agpVersion)
      }
    }
    builder.withAndroidSubproject('app') { s ->
      s.manifest = AndroidManifest.app('com.example.MainApplication')
      s.withBuildScript { bs ->
        bs.plugins = [Plugin.androidAppPlugin]
        bs.dependencies = [
          appcompat('implementation'),
          timber('implementation')
        ]
      }
    }

    def project = builder.build()
    project.writer().write()
    return project
  }

  static ComprehensiveAdvice removeTimberAdvice() {
    return new ComprehensiveAdvice(
      ':app',
      [Advice.ofRemove(new Dependency(
        'com.jakewharton.timber:timber', '4.7.1', 'implementation'
      ))] as Set<Advice>,
      [] as Set<PluginAdvice>,
      false
    )
  }
}
