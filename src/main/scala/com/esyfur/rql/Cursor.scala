package com.esyfur.rql


import com.rethinkdb.{Ql2 => p}
import scala.collection.mutable.ArrayBuffer

class Cursor(
    private val connection: Connection,
    private val response: p.Response,
    private val chunk: Any
    ) extends Iterator[Any] with AutoCloseable {

    private var index = 0

    private def complete = (response.getType != p.Response.ResponseType.SUCCESS_PARTIAL)

    override def size: Int = response.getResponseCount

    def hasNext: Boolean = (!complete || index < size)

    def next(): Any = {
        if (index == size) {
            if (!complete) readMore()
        }
        else {
            index += 1
            chunk
        }
    }

    private def readMore(): Unit = {
        connection.continue(response.getToken)
    }

    def generatedKey: String = next().asInstanceOf[Document].get("generated_keys").get.asInstanceOf[ArrayBuffer[String]].head

    def each() = ???

    def toArray() = ???

    def close(): Unit = connection.terminate(response.getToken)

}
