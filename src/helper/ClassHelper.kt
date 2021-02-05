import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

object ClassHelper {

    fun getPropertyValue(instance: Any, property: KProperty1<Any, *>): String {
        return property.get(instance).toString()
    }

    fun <T> getAllPropertyValues(instance: Any): MutableList<T> {
        var inputs: MutableList<String> = ArrayList()
        instance::class.memberProperties.forEach {
            val propertyValue = getPropertyValue(instance, it as KProperty1<Any, *>)
            inputs.add(propertyValue)
        }

        return inputs as MutableList<T>
    }
}
