/*
 * Copyright 2017 Barclays Africa Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package za.co.absa.spline.core.conf

import org.apache.commons.configuration.Configuration
import org.apache.spark.sql.SparkSession
import org.slf4s.Logging
import za.co.absa.spline.common.transformations.AsyncTransformationPipeline
import za.co.absa.spline.core.LogicalPlanLineageHarvester
import za.co.absa.spline.core.batch.BatchListener
import za.co.absa.spline.core.conf.SplineConfigurer.SplineMode
import za.co.absa.spline.core.conf.SplineConfigurer.SplineMode._
import za.co.absa.spline.core.streaming.{StreamWriteOperationHarvester, StructuredStreamingLineageHarvester, StructuredStreamingListener}
import za.co.absa.spline.core.transformations.{ForeignMetaDatasetInjector, LineageProjectionMerger}
import za.co.absa.spline.persistence.api.{NopDataLineageReader, PersistenceFactory}

import scala.concurrent.ExecutionContext

/**
  * The object contains static information about default settings needed for initialization of the library.
  */
object DefaultSplineConfigurer {

  //noinspection TypeAnnotation
  object ConfProperty {
    val PERSISTENCE_FACTORY = "spline.persistence.factory"

    /**
      * How Spline should behave.
      *
      * @see [[SplineMode]]
      */
    val MODE = "spline.mode"
    val MODE_DEFAULT = BEST_EFFORT.toString
  }

}

/**
  * The class represents default settings needed for initialization of the library.
  *
  * @param configuration A source of settings
  */
class DefaultSplineConfigurer(configuration: Configuration, sparkSession: SparkSession) extends SplineConfigurer with Logging {

  import DefaultSplineConfigurer.ConfProperty._
  import za.co.absa.spline.common.ConfigurationImplicits._
  private implicit val executionContext: ExecutionContext = ExecutionContext.global

  lazy val splineMode: SplineMode = {
    val modeName = configuration.getString(MODE, MODE_DEFAULT)
    try SplineMode withName modeName
    catch {
      case _: NoSuchElementException => throw new IllegalArgumentException(
        s"Invalid value for property $MODE=$modeName. Should be one of: ${SplineMode.values mkString ", "}")
    }
  }

  private def persistenceFactory: PersistenceFactory = {
    val persistenceFactoryClassName = configuration getRequiredString PERSISTENCE_FACTORY

    log debug s"Instantiating persistence factory: $persistenceFactoryClassName"

    Class.forName(persistenceFactoryClassName)
      .getConstructor(classOf[Configuration])
      .newInstance(configuration)
      .asInstanceOf[PersistenceFactory]
  }



  // commons
  private lazy val dataLineageReader = persistenceFactory.createDataLineageReader.getOrElse(NopDataLineageReader)

  private lazy val dataLineageWriter = persistenceFactory.createDataLineageWriter

  private lazy val logicalPlanHarvester = new LogicalPlanLineageHarvester(sparkSession.sparkContext.hadoopConfiguration)

  private lazy val transformationPipeline =
    new AsyncTransformationPipeline(
      LineageProjectionMerger,
      new ForeignMetaDatasetInjector(dataLineageReader)
    )

  // batch
  lazy val batchListener = new BatchListener(dataLineageReader, dataLineageWriter, logicalPlanHarvester, transformationPipeline)

  // streaming
  private lazy val streamWriteOperationHarvester = new StreamWriteOperationHarvester

  private lazy val structuredStreamingHarvester = new StructuredStreamingLineageHarvester(logicalPlanHarvester, streamWriteOperationHarvester)

  lazy val structuredStreamingListener = new StructuredStreamingListener(sparkSession.streams, structuredStreamingHarvester, dataLineageWriter)

}
