package com.github.khousehold.flax.spring.web

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.github.khousehold.oink.commons.filters.models.Filter
import com.github.khousehold.oink.commons.filters.models.IFilter
import com.github.khousehold.oink.commons.filters.models.LogicalFilter
import com.github.khousehold.oink.commons.filters.models.LogicalFilterType
import com.github.khousehold.oink.commons.utils.toList

class FilterDeserializer() : JsonDeserializer<IFilter>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): IFilter {
    val oc = p!!.codec
    val node: JsonNode = oc.readTree(p)

    return deserialize(node, p)
  }

  fun deserialize(node: JsonNode, jp: JsonParser): IFilter {
    return if(node.has("type")) {
      // This is a Logical filter
      val typeName = node.get("type").asText()
      val subFilters: List<IFilter> = if(node.has("filters"))
        (node.get("filters") as ArrayNode)
            .elements()
            .toList()
            .map { deserialize(it, jp) }
      else listOf()
      LogicalFilter(LogicalFilterType.valueOf(typeName), subFilters)
    } else {
      val mapper = ObjectMapper()
      mapper.readValue(node.toString(), Filter::class.java)
    }
  }
}