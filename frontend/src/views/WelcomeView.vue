<script setup>

</script>

<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1;background-color: black">
      <el-image style="width: 100%;height: 100%" fit="cover"
                src="src/assets/welcome-image.png"/>
    </div>
    <div class="welcome-title">
      <div>welcome here.</div>
    </div>
    <div class="right-card">
      <router-view/>
    </div>
  </div>
</template>

<style scoped>
.right-card {
  width: 400px;
  z-index: 1;
  background-color: white;
}

.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 120px;
  transform: translate(-50%, -50%);
  font-weight: bold;
  color: white;
  text-shadow: 0 0 10px black;
}
</style>
