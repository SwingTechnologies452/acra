/*
 *  Copyright 2016
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.acra.collector

import android.content.Context
import org.acra.ReportField
import org.acra.builder.ReportBuilder
import org.acra.config.CoreConfiguration
import org.acra.data.CrashReportData

/**
 * Base implementation of a collector.
 * Maintains information on which fields can be collected by this collector.
 * Validates constraints in which a field should (not) be collected.
 *
 * @author F43nd1r
 * @since 4.9.1
 */
abstract class BaseReportFieldCollector(private vararg val reportFields: ReportField) : Collector {

    /**
     * this should check if the config contains the field, but may add additional checks like permissions etc.
     *
     * @param context       a context
     * @param config        current configuration
     * @param collect       the field to collect
     * @param reportBuilder the current reportBuilder
     * @return if this field should be collected now
     */
    open fun shouldCollect(context: Context, config: CoreConfiguration, collect: ReportField, reportBuilder: ReportBuilder): Boolean {
        return config.reportContent.contains(collect)
    }

    /**
     * Calls [.shouldCollect] for each ReportField
     * and then [.collect] if it returned true
     */
    @Throws(CollectorException::class)
    override fun collect(context: Context, config: CoreConfiguration, reportBuilder: ReportBuilder, crashReportData: CrashReportData) {
        for (field in reportFields) {
            try {
                if (shouldCollect(context, config, field, reportBuilder)) {
                    collect(field, context, config, reportBuilder, crashReportData)
                }
            } catch (t: Exception) {
                crashReportData.put(field, null as String?)
                throw CollectorException("Error while retrieving " + field.name + " data:" + t.message, t)
            }
        }
    }

    /**
     * Collect a ReportField
     *
     * @param reportField the reportField to collect
     * @param context a context
     * @param config current Configuration
     * @param reportBuilder current ReportBuilder
     * @param target put results here
     * @throws Exception if collection failed
     */
    @Throws(Exception::class)
    abstract fun collect(reportField: ReportField, context: Context, config: CoreConfiguration, reportBuilder: ReportBuilder, target: CrashReportData)
}