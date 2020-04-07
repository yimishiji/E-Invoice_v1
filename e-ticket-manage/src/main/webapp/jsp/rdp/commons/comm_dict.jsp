<%@ taglib prefix="ps" uri="/WEB-INF/tld/permission.tld"%>
<div style="display: none;" id="renderData">
	<ps:foreach items="${param.ref1 }" var="item">
		<span code="${item.value }">${item.key }</span>
	</ps:foreach>
	<ps:foreach items="${param.ref2 }" var="item">
		<span code="${item.value }">${item.key }</span>
	</ps:foreach>
</div>